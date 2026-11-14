<script lang="ts">
  import { api, errorMessage } from '../lib/api';
  import { auth } from '../lib/auth.svelte';
  import { planStats } from '../lib/format';
  import type { Workout } from '../lib/types';
  import LoginPrompt from '../components/LoginPrompt.svelte';
  import WorkoutCard from '../components/WorkoutCard.svelte';

  let plan = $state<Workout[]>([]);
  let loading = $state(true);
  let error = $state('');
  let busyId = $state<string | null>(null);

  const stats = $derived(planStats(plan));

  $effect(() => {
    if (auth.ready && auth.user) {
      load();
    }
  });

  async function load() {
    loading = true;
    try {
      plan = await api.plan();
    } catch (e) {
      error = errorMessage(e);
    } finally {
      loading = false;
    }
  }

  async function remove(workout: Workout) {
    busyId = workout.id;
    try {
      await api.removeFromPlan(workout.id);
      plan = plan.filter((w) => w.id !== workout.id);
      await auth.refreshMe();
    } catch (e) {
      error = errorMessage(e);
    } finally {
      busyId = null;
    }
  }
</script>

<section class="page">
  <div class="page-header">
    <div>
      <p class="eyebrow">Your training</p>
      <h1>My plan</h1>
      <p>The exercises you picked from the library.</p>
    </div>
  </div>

  {#if !auth.ready}
    <div class="skeleton"></div>
  {:else if !auth.user}
    <LoginPrompt message="Log in to build your personal training plan." />
  {:else}
    {#if error}
      <div class="notice notice-error">{error}</div>
    {/if}

    <dl class="card stats">
      <div>
        <dt>Exercises</dt>
        <dd>{stats.exercises}</dd>
      </div>
      <div>
        <dt>Total sets</dt>
        <dd>{stats.sets}</dd>
      </div>
      <div>
        <dt>Total reps</dt>
        <dd>{stats.reps}</dd>
      </div>
    </dl>

    {#if loading && plan.length === 0}
      <div class="skeleton"></div>
    {:else if plan.length === 0}
      <div class="card empty">
        <p>Your plan is empty.</p>
        <a class="btn btn-primary" href="#/workouts">Browse workouts</a>
      </div>
    {:else}
      <div class="grid">
        {#each plan as workout (workout.id)}
          <WorkoutCard
            {workout}
            canDelete
            deleteLabel="Remove"
            busy={busyId === workout.id}
            onDelete={remove}
          />
        {/each}
      </div>
    {/if}
  {/if}
</section>

<style>
  .stats {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    margin: 0;
  }

  .stats dt {
    color: var(--muted);
    font-size: 0.8rem;
    text-transform: uppercase;
    letter-spacing: 0.1em;
    font-weight: 600;
  }

  .stats dd {
    margin: 0.2rem 0 0;
    font-size: 1.8rem;
    font-weight: 800;
  }
</style>
