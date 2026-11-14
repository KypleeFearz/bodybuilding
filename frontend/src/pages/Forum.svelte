<script lang="ts">
  import { onMount } from 'svelte';
  import { api, ApiError, errorMessage } from '../lib/api';
  import { auth } from '../lib/auth.svelte';
  import { timeAgo } from '../lib/format';
  import { router } from '../lib/router.svelte';
  import type { ThreadSummary } from '../lib/types';
  import LoginPrompt from '../components/LoginPrompt.svelte';

  let threads = $state<ThreadSummary[]>([]);
  let loading = $state(true);
  let error = $state('');

  let showForm = $state(false);
  let title = $state('');
  let content = $state('');
  let fieldErrors = $state<Record<string, string>>({});
  let saving = $state(false);

  onMount(async () => {
    try {
      threads = await api.threads();
    } catch (e) {
      error = errorMessage(e);
    } finally {
      loading = false;
    }
  });

  async function create(event: SubmitEvent) {
    event.preventDefault();
    saving = true;
    fieldErrors = {};
    try {
      const thread = await api.createThread(title, content);
      router.go(`/forum/${thread.id}`);
    } catch (e) {
      if (e instanceof ApiError) fieldErrors = e.fieldErrors;
      error = errorMessage(e);
    } finally {
      saving = false;
    }
  }
</script>

<section class="page">
  <div class="page-header">
    <div>
      <p class="eyebrow">Community</p>
      <h1>Forum</h1>
      <p>Questions, progress and programming talk.</p>
    </div>
    {#if auth.user}
      <button class="btn {showForm ? 'btn-ghost' : 'btn-primary'}" onclick={() => (showForm = !showForm)}>
        {showForm ? 'Cancel' : '+ New thread'}
      </button>
    {/if}
  </div>

  {#if showForm}
    <form class="card form" onsubmit={create}>
      <label>
        Title
        <input bind:value={title} required maxlength="120" placeholder="What do you want to discuss?" />
        {#if fieldErrors.title}<span class="field-error">{fieldErrors.title}</span>{/if}
      </label>
      <label>
        Message
        <textarea bind:value={content} required maxlength="5000"></textarea>
        {#if fieldErrors.content}<span class="field-error">{fieldErrors.content}</span>{/if}
      </label>
      <div class="row">
        <button class="btn btn-primary" type="submit" disabled={saving}>
          {saving ? 'Posting…' : 'Post thread'}
        </button>
      </div>
    </form>
  {/if}

  {#if error}
    <div class="notice notice-error">{error}</div>
  {/if}

  {#if loading}
    <div class="skeleton"></div>
  {:else if threads.length === 0}
    <div class="card empty"><p>No threads yet. Start the first discussion!</p></div>
  {:else}
    <ul class="threads">
      {#each threads as thread (thread.id)}
        <li>
          <a class="card card-interactive thread" href={`#/forum/${thread.id}`}>
            <div>
              <h3>{thread.title}</h3>
              <p class="muted small">
                by {thread.authorName} · active {timeAgo(thread.lastActivityAt)}
              </p>
            </div>
            <span class="replies" title="Replies">
              <strong>{thread.replies}</strong>
              {thread.replies === 1 ? 'reply' : 'replies'}
            </span>
          </a>
        </li>
      {/each}
    </ul>
  {/if}

  {#if auth.ready && !auth.user}
    <LoginPrompt message="Log in to start a thread or reply." />
  {/if}
</section>

<style>
  .threads {
    list-style: none;
    margin: 0;
    padding: 0;
    display: grid;
    gap: 0.75rem;
  }

  .thread {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 1rem;
  }

  h3 {
    font-size: 1.1rem;
    margin-bottom: 0.25rem;
  }

  .replies {
    color: var(--muted);
    font-size: 0.85rem;
    text-align: right;
    white-space: nowrap;
  }

  .replies strong {
    display: block;
    color: var(--text);
    font-size: 1.3rem;
  }
</style>
