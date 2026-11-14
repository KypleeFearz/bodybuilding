<script lang="ts">
  import { onMount } from 'svelte';
  import { api } from './lib/api';
  import { auth } from './lib/auth.svelte';
  import { router } from './lib/router.svelte';
  import Home from './pages/Home.svelte';
  import Workouts from './pages/Workouts.svelte';
  import Plan from './pages/Plan.svelte';
  import Forum from './pages/Forum.svelte';
  import Thread from './pages/Thread.svelte';
  import AdminUsers from './pages/AdminUsers.svelte';
  import NotFound from './pages/NotFound.svelte';

  let backendDown = $state(false);

  onMount(async () => {
    try {
      await auth.init(await api.config());
    } catch {
      backendDown = true;
      auth.ready = true;
    }
  });

  const links = $derived([
    { path: '/workouts', label: 'Workouts', active: router.route.name === 'workouts', show: true },
    { path: '/plan', label: 'My plan', active: router.route.name === 'plan', show: auth.enabled },
    {
      path: '/forum',
      label: 'Forum',
      active: router.route.name === 'forum' || router.route.name === 'thread',
      show: true,
    },
    { path: '/admin', label: 'Admin', active: router.route.name === 'admin', show: auth.isAdmin },
  ]);

  const displayName = $derived(auth.me?.username ?? auth.user?.nickname ?? auth.user?.name ?? '');
</script>

<header class="topbar">
  <div class="container topbar-inner">
    <a class="brand" href="#/">
      <img src="/favicon.svg" alt="" width="30" height="30" />
      <span>Bodybuilding</span>
    </a>

    <nav aria-label="Main">
      {#each links.filter((l) => l.show) as link (link.path)}
        <a href={`#${link.path}`} class:active={link.active} aria-current={link.active ? 'page' : undefined}>
          {link.label}
        </a>
      {/each}
    </nav>

    <div class="session">
      {#if !auth.ready}
        <span class="badge">…</span>
      {:else if !auth.enabled}
        <span class="badge" title="Auth0 is not configured, the app is read-only">Demo mode</span>
      {:else if auth.user}
        <span class="avatar" aria-hidden="true">{displayName.charAt(0).toUpperCase()}</span>
        <span class="name">{displayName}</span>
        <button class="btn btn-ghost btn-sm" onclick={() => auth.logout()}>Log out</button>
      {:else}
        <button class="btn btn-primary btn-sm" onclick={() => auth.login()}>Log in</button>
      {/if}
    </div>
  </div>
</header>

<main class="container">
  {#if backendDown}
    <div class="notice notice-error" style="margin-top: 1.5rem">
      The API is not reachable. Start the backend (see README) and reload the page.
    </div>
  {/if}

  {#if router.route.name === 'home'}
    <Home />
  {:else if router.route.name === 'workouts'}
    <Workouts />
  {:else if router.route.name === 'plan'}
    <Plan />
  {:else if router.route.name === 'forum'}
    <Forum />
  {:else if router.route.name === 'thread'}
    {#key router.route.id}
      <Thread id={router.route.id} />
    {/key}
  {:else if router.route.name === 'admin'}
    <AdminUsers />
  {:else}
    <NotFound />
  {/if}
</main>

<footer class="container footer">
  <span>Bodybuilding · Spring Boot, MongoDB and Svelte</span>
  <a href="/swagger-ui.html" target="_blank" rel="noreferrer">API docs</a>
</footer>

<style>
  .topbar {
    position: sticky;
    top: 0;
    z-index: 10;
    backdrop-filter: blur(12px);
    background: rgb(12 14 17 / 0.8);
    border-bottom: 1px solid var(--border);
  }

  .topbar-inner {
    display: flex;
    align-items: center;
    gap: 1.5rem;
    height: 64px;
  }

  .brand {
    display: flex;
    align-items: center;
    gap: 0.6rem;
    font-weight: 800;
    letter-spacing: -0.02em;
    font-size: 1.05rem;
  }

  nav {
    display: flex;
    gap: 0.25rem;
    overflow-x: auto;
  }

  nav a {
    padding: 0.45rem 0.85rem;
    border-radius: 999px;
    color: var(--muted);
    font-weight: 600;
    font-size: 0.925rem;
  }

  nav a:hover {
    color: var(--text);
  }

  nav a.active {
    color: var(--text);
    background: var(--surface-2);
  }

  .session {
    margin-left: auto;
    display: flex;
    align-items: center;
    gap: 0.6rem;
  }

  .avatar {
    display: grid;
    place-items: center;
    width: 30px;
    height: 30px;
    border-radius: 50%;
    background: var(--accent);
    color: var(--accent-ink);
    font-weight: 800;
    font-size: 0.85rem;
  }

  .footer {
    display: flex;
    justify-content: space-between;
    gap: 1rem;
    padding-block: 2rem 2.5rem;
    border-top: 1px solid var(--border);
    color: var(--muted);
    font-size: 0.85rem;
  }

  .footer a:hover {
    color: var(--text);
  }

  @media (max-width: 720px) {
    .topbar-inner {
      gap: 0.75rem;
    }

    .brand span,
    .name {
      display: none;
    }
  }
</style>
