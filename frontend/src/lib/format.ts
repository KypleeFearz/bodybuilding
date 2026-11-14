import type { MuscleGroup, Workout } from './types';

export function muscleGroupLabel(group: MuscleGroup): string {
  if (group === 'FULL_BODY') return 'Full body';
  return group.charAt(0) + group.slice(1).toLowerCase();
}

/** Short relative time such as "5 min ago". Older dates fall back to an absolute date. */
export function timeAgo(iso: string, now: Date = new Date()): string {
  const seconds = Math.round((now.getTime() - new Date(iso).getTime()) / 1000);
  if (seconds < 60) return 'just now';

  const minutes = Math.floor(seconds / 60);
  if (minutes < 60) return `${minutes} min ago`;

  const hours = Math.floor(minutes / 60);
  if (hours < 24) return `${hours} h ago`;

  const days = Math.floor(hours / 24);
  if (days === 1) return 'yesterday';
  if (days < 30) return `${days} days ago`;

  return new Date(iso).toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' });
}

export function planStats(workouts: Workout[]): { exercises: number; sets: number; reps: number } {
  return {
    exercises: workouts.length,
    sets: workouts.reduce((sum, w) => sum + w.sets, 0),
    reps: workouts.reduce((sum, w) => sum + w.sets * w.reps, 0),
  };
}
