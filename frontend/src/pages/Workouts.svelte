<script lang="ts">
  import { api, ApiError, errorMessage } from '../lib/api';
  import { auth } from '../lib/auth.svelte';
  import { muscleGroupLabel } from '../lib/format';
  import { MUSCLE_GROUPS, type MuscleGroup, type Workout, type WorkoutInput } from '../lib/types';
  import WorkoutCard from '../components/WorkoutCard.svelte';

  const emptyForm = (): WorkoutInput => ({
    exercise: '',
    sets: 3,
    reps: 10,
    muscleGroup: 'CHEST',
    notes: '',
  });

  let filter = $state<MuscleGroup | undefined>();
  let workouts = $state<Workout[]>([]);
  let loading = $state(true);
  let error = $state('');
  let busyId = $state<string | null>(null);

  let showForm = $state(false);
  let form = $state<WorkoutInput>(emptyForm());
  let fieldErrors = $state<Record<string, string>>({});
  let saving = $state(false);

  const saved = $derived(new Set(auth.me?.savedWorkoutIds ?? []));

  async function load(muscleGroup: MuscleGroup | undefined) {
    loading = true;
    error = '';
    try {
      workouts = await api.workouts(muscleGroup);
    } catch (e) {
      error = errorMessage(e);
    } finally {
      loading = false;
    }
  }

  $effect(() => {
    load(filter);
  });

  async function togglePlan(workout: Workout) {
    if (!auth.user) {
      await auth.login();
      return;
    }
    busyId = workout.id;
    try {
      if (saved.has(workout.id)) {
        await api.removeFromPlan(workout.id);
      } else {
        await api.addToPlan(workout.id);
      }
      await auth.refreshMe();
    } catch (e) {
      error = errorMessage(e);
    } finally {
      busyId = null;
    }
  }

  async function remove(workout: Workout) {
    if (!confirm(`Delete "${workout.exercise}" from the library?`)) return;
    busyId = workout.id;
    try {
      await api.deleteWorkout(workout.id);
      workouts = workouts.filter((w) => w.id !== workout.id);
      await auth.refreshMe();
    } catch (e) {
      error = errorMessage(e);
    } finally {
      busyId = null;
    }
  }

  async function create(event: SubmitEvent) {
    event.preventDefault();
    saving = true;
    fieldErrors = {};
    error = '';
    try {
      await api.createWorkout({ ...form, notes: form.notes?.trim() || undefined });
      form = emptyForm();
      showForm = false;
      await load(filter);
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
      <p class="eyebrow">Library</p>
      <h1>Workouts</h1>
      <p>Pick the exercises you want to train and add them to your plan.</p>
    </div>
    {#if auth.isAdmin}
      <button class="btn {showForm ? 'btn-ghost' : 'btn-primary'}" onclick={() => (showForm = !showForm)}>
        {showForm ? 'Cancel' : '+ New workout'}
      </button>
    {/if}
  </div>

  {#if showForm}
    <form class="card form" onsubmit={create}>
      <div class="form-grid">
        <label>
          Exercise
          <input bind:value={form.exercise} required maxlength="80" placeholder="e.g. Front Squat" />
          {#if fieldErrors.exercise}<span class="field-error">{fieldErrors.exercise}</span>{/if}
        </label>
        <label>
          Muscle group
          <select bind:value={form.muscleGroup}>
            {#each MUSCLE_GROUPS as group (group)}
              <option value={group}>{muscleGroupLabel(group)}</option>
            {/each}
          </select>
        </label>
        <label>
          Sets
          <input type="number" bind:value={form.sets} min="1" max="20" required />
          {#if fieldErrors.sets}<span class="field-error">{fieldErrors.sets}</span>{/if}
        </label>
        <label>
          Reps
          <input type="number" bind:value={form.reps} min="1" max="100" required />
          {#if fieldErrors.reps}<span class="field-error">{fieldErrors.reps}</span>{/if}
        </label>
      </div>
      <label>
        Coaching notes (optional)
        <input bind:value={form.notes} maxlength="500" placeholder="Cues, tempo, variations" />
      </label>
      <div class="row">
        <button class="btn btn-primary" type="submit" disabled={saving}>
          {saving ? 'Saving…' : 'Create workout'}
        </button>
      </div>
    </form>
  {/if}

  <div class="chips" role="group" aria-label="Filter by muscle group">
    <button class="chip" aria-pressed={filter === undefined} onclick={() => (filter = undefined)}>All</button>
    {#each MUSCLE_GROUPS as group (group)}
      <button class="chip" aria-pressed={filter === group} onclick={() => (filter = group)}>
        {muscleGroupLabel(group)}
      </button>
    {/each}
  </div>

  {#if error}
    <div class="notice notice-error">{error}</div>
  {/if}

  {#if loading && workouts.length === 0}
    <div class="grid">
      {#each { length: 6 } as _, i (i)}
        <div class="skeleton"></div>
      {/each}
    </div>
  {:else if workouts.length === 0}
    <div class="card empty">
      <p>No workouts {filter ? `for ${muscleGroupLabel(filter).toLowerCase()}` : 'yet'}.</p>
    </div>
  {:else}
    <div class="grid">
      {#each workouts as workout (workout.id)}
        <WorkoutCard
          {workout}
          saved={saved.has(workout.id)}
          showPlanButton={auth.enabled}
          canDelete={auth.isAdmin}
          busy={busyId === workout.id}
          onToggle={togglePlan}
          onDelete={remove}
        />
      {/each}
    </div>
  {/if}
</section>
