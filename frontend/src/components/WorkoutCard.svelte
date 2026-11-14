<script lang="ts">
  import { muscleGroupLabel } from '../lib/format';
  import type { Workout } from '../lib/types';

  interface Props {
    workout: Workout;
    saved?: boolean;
    showPlanButton?: boolean;
    canDelete?: boolean;
    busy?: boolean;
    onToggle?: (workout: Workout) => void;
    onDelete?: (workout: Workout) => void;
    deleteLabel?: string;
  }

  let {
    workout,
    saved = false,
    showPlanButton = false,
    canDelete = false,
    busy = false,
    onToggle,
    onDelete,
    deleteLabel = 'Delete',
  }: Props = $props();
</script>

<article class="card card-interactive workout">
  <div class="top">
    <span class="tag">{muscleGroupLabel(workout.muscleGroup)}</span>
    {#if canDelete}
      <button class="btn btn-danger btn-sm" disabled={busy} onclick={() => onDelete?.(workout)}>
        {deleteLabel}
      </button>
    {/if}
  </div>

  <h3>{workout.exercise}</h3>

  <p class="volume">
    <strong>{workout.sets}</strong> sets <span class="x">×</span> <strong>{workout.reps}</strong> reps
  </p>

  {#if workout.notes}
    <p class="muted small">{workout.notes}</p>
  {/if}

  {#if showPlanButton}
    <button
      class="btn btn-sm plan-btn {saved ? 'btn-active' : 'btn-ghost'}"
      disabled={busy}
      aria-pressed={saved}
      onclick={() => onToggle?.(workout)}
    >
      {saved ? '✓ In your plan' : '+ Add to plan'}
    </button>
  {/if}
</article>

<style>
  .workout {
    display: grid;
    gap: 0.6rem;
    align-content: start;
  }

  .top {
    display: flex;
    justify-content: space-between;
    align-items: center;
    min-height: 30px;
  }

  h3 {
    font-size: 1.15rem;
  }

  .volume {
    color: var(--muted);
  }

  .volume strong {
    color: var(--text);
    font-size: 1.4rem;
    font-weight: 800;
  }

  .x {
    color: var(--accent);
    margin-inline: 0.15rem;
  }

  .plan-btn {
    justify-self: start;
    margin-top: 0.35rem;
  }
</style>
