<script lang="ts">
  import { onMount } from 'svelte';
  import { api, ApiError, errorMessage } from '../lib/api';
  import { auth } from '../lib/auth.svelte';
  import { timeAgo } from '../lib/format';
  import { router } from '../lib/router.svelte';
  import type { Post, Thread } from '../lib/types';
  import LoginPrompt from '../components/LoginPrompt.svelte';

  let { id }: { id: string } = $props();

  let thread = $state<Thread | null>(null);
  let notFound = $state(false);
  let error = $state('');

  let reply = $state('');
  let sending = $state(false);
  let editingId = $state<string | null>(null);
  let editText = $state('');

  const myId = $derived(auth.me?.id);
  const canDeleteThread = $derived(!!thread && (auth.isAdmin || thread.authorId === myId));

  onMount(load);

  async function load() {
    try {
      thread = await api.thread(id);
    } catch (e) {
      if (e instanceof ApiError && e.status === 404) notFound = true;
      else error = errorMessage(e);
    }
  }

  async function run(action: () => Promise<unknown>) {
    error = '';
    try {
      await action();
      await load();
    } catch (e) {
      error = errorMessage(e);
    }
  }

  async function sendReply(event: SubmitEvent) {
    event.preventDefault();
    sending = true;
    await run(async () => {
      await api.addPost(id, reply);
      reply = '';
    });
    sending = false;
  }

  function startEdit(post: Post) {
    editingId = post.id;
    editText = post.content;
  }

  async function saveEdit(post: Post) {
    await run(() => api.editPost(id, post.id, editText));
    editingId = null;
  }

  async function deletePost(post: Post) {
    if (!confirm('Delete this post?')) return;
    await run(() => api.deletePost(id, post.id));
  }

  async function deleteThread() {
    if (!confirm('Delete the whole thread?')) return;
    try {
      await api.deleteThread(id);
      router.go('/forum');
    } catch (e) {
      error = errorMessage(e);
    }
  }
</script>

<section class="page">
  <a class="back muted small" href="#/forum">← All threads</a>

  {#if notFound}
    <div class="card empty"><p>This thread does not exist (anymore).</p></div>
  {:else if !thread}
    {#if error}
      <div class="notice notice-error">{error}</div>
    {:else}
      <div class="skeleton"></div>
    {/if}
  {:else}
    <div class="page-header">
      <div>
        <h1>{thread.title}</h1>
        <p>Started by {thread.authorName} · {timeAgo(thread.createdAt)}</p>
      </div>
      {#if canDeleteThread}
        <button class="btn btn-danger" onclick={deleteThread}>Delete thread</button>
      {/if}
    </div>

    {#if error}
      <div class="notice notice-error">{error}</div>
    {/if}

    <ol class="posts">
      {#each thread.posts as post (post.id)}
        <li class="card post" class:mine={post.authorId === myId}>
          <header class="row">
            <span class="avatar" aria-hidden="true">{post.authorName.charAt(0).toUpperCase()}</span>
            <strong>{post.authorName}</strong>
            <span class="muted small">
              {timeAgo(post.createdAt)}{post.editedAt ? ' · edited' : ''}
            </span>
            <span class="actions">
              {#if post.authorId === myId && editingId !== post.id}
                <button class="btn btn-ghost btn-sm" onclick={() => startEdit(post)}>Edit</button>
              {/if}
              {#if post.authorId === myId || auth.isAdmin}
                <button class="btn btn-danger btn-sm" onclick={() => deletePost(post)}>Delete</button>
              {/if}
            </span>
          </header>

          {#if editingId === post.id}
            <div class="stack">
              <textarea bind:value={editText} maxlength="5000"></textarea>
              <div class="row">
                <button
                  class="btn btn-primary btn-sm"
                  onclick={() => saveEdit(post)}
                  disabled={!editText.trim()}
                >
                  Save
                </button>
                <button class="btn btn-ghost btn-sm" onclick={() => (editingId = null)}>Cancel</button>
              </div>
            </div>
          {:else}
            <p class="content">{post.content}</p>
          {/if}
        </li>
      {/each}
    </ol>

    {#if auth.user}
      <form class="card form" onsubmit={sendReply}>
        <label>
          Your reply
          <textarea bind:value={reply} required maxlength="5000" placeholder="Share your experience…"
          ></textarea>
        </label>
        <div class="row">
          <button class="btn btn-primary" type="submit" disabled={sending || !reply.trim()}>
            {sending ? 'Sending…' : 'Reply'}
          </button>
        </div>
      </form>
    {:else if auth.ready}
      <LoginPrompt message="Log in to join the discussion." />
    {/if}
  {/if}
</section>

<style>
  .back:hover {
    color: var(--text);
  }

  .posts {
    list-style: none;
    margin: 0;
    padding: 0;
    display: grid;
    gap: 0.75rem;
  }

  .post {
    display: grid;
    gap: 0.75rem;
  }

  .post.mine {
    border-color: rgb(214 255 61 / 0.25);
  }

  .avatar {
    display: grid;
    place-items: center;
    width: 28px;
    height: 28px;
    border-radius: 50%;
    background: var(--surface-2);
    border: 1px solid var(--border);
    font-weight: 700;
    font-size: 0.8rem;
  }

  .actions {
    margin-left: auto;
    display: flex;
    gap: 0.25rem;
  }

  .content {
    white-space: pre-wrap;
    overflow-wrap: anywhere;
  }
</style>
