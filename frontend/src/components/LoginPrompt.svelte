<script lang="ts">
  import { auth } from '../lib/auth.svelte';

  let { message }: { message: string } = $props();
</script>

<div class="card empty">
  {#if auth.enabled}
    <p>{message}</p>
    <button class="btn btn-primary" onclick={() => auth.login()}>Log in</button>
  {:else}
    <p>Login is disabled because Auth0 is not configured. The app runs in read-only demo mode.</p>
  {/if}
</div>
