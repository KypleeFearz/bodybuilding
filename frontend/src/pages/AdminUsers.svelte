<script lang="ts">
  import { api, errorMessage } from '../lib/api';
  import { auth } from '../lib/auth.svelte';
  import { timeAgo } from '../lib/format';
  import type { UserProfile } from '../lib/types';

  let users = $state<UserProfile[]>([]);
  let loading = $state(true);
  let error = $state('');

  $effect(() => {
    if (auth.isAdmin) load();
  });

  async function load() {
    try {
      users = await api.users();
    } catch (e) {
      error = errorMessage(e);
    } finally {
      loading = false;
    }
  }

  async function remove(user: UserProfile) {
    if (!confirm(`Delete the profile of ${user.username}?`)) return;
    try {
      await api.deleteUser(user.id);
      users = users.filter((u) => u.id !== user.id);
    } catch (e) {
      error = errorMessage(e);
    }
  }
</script>

<section class="page">
  <div class="page-header">
    <div>
      <p class="eyebrow">Admin</p>
      <h1>Users</h1>
      <p>Profiles are created automatically on first login.</p>
    </div>
  </div>

  {#if !auth.ready}
    <div class="skeleton"></div>
  {:else if !auth.isAdmin}
    <div class="card empty"><p>This page is only available to admins.</p></div>
  {:else}
    {#if error}
      <div class="notice notice-error">{error}</div>
    {/if}
    <div class="card table-wrap">
      {#if loading}
        <p class="muted">Loading…</p>
      {:else}
        <table class="table">
          <thead>
            <tr>
              <th>User</th>
              <th>E-mail</th>
              <th>Plan</th>
              <th>Joined</th>
              <th><span class="sr-only">Actions</span></th>
            </tr>
          </thead>
          <tbody>
            {#each users as user (user.id)}
              <tr>
                <td><strong>{user.username}</strong></td>
                <td class="muted">{user.email ?? '–'}</td>
                <td>{user.savedWorkouts} workouts</td>
                <td class="muted">{timeAgo(user.createdAt)}</td>
                <td class="right">
                  {#if user.id !== auth.me?.id}
                    <button class="btn btn-danger btn-sm" onclick={() => remove(user)}>Delete</button>
                  {/if}
                </td>
              </tr>
            {/each}
          </tbody>
        </table>
      {/if}
    </div>
  {/if}
</section>

<style>
  .table-wrap {
    overflow-x: auto;
    padding: 0.5rem 0.75rem;
  }

  .right {
    text-align: right;
  }

  .sr-only {
    position: absolute;
    width: 1px;
    height: 1px;
    overflow: hidden;
    clip-path: inset(50%);
  }
</style>
