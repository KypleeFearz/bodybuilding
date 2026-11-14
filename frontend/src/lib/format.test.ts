import { describe, expect, it } from 'vitest';
import { muscleGroupLabel, planStats, timeAgo } from './format';
import type { Workout } from './types';

describe('muscleGroupLabel', () => {
  it('formats enum values for display', () => {
    expect(muscleGroupLabel('CHEST')).toBe('Chest');
    expect(muscleGroupLabel('FULL_BODY')).toBe('Full body');
  });
});

describe('timeAgo', () => {
  const now = new Date('2026-11-14T12:00:00Z');

  it.each([
    ['2026-11-14T11:59:30Z', 'just now'],
    ['2026-11-14T11:45:00Z', '15 min ago'],
    ['2026-11-14T09:00:00Z', '3 h ago'],
    ['2026-11-13T10:00:00Z', 'yesterday'],
    ['2026-11-04T12:00:00Z', '10 days ago'],
  ])('formats %s as "%s"', (iso, expected) => {
    expect(timeAgo(iso, now)).toBe(expected);
  });

  it('falls back to an absolute date for old timestamps', () => {
    expect(timeAgo('2025-01-05T12:00:00Z', now)).toBe('5 Jan 2025');
  });
});

describe('planStats', () => {
  const workout = (sets: number, reps: number): Workout => ({
    id: `${sets}x${reps}`,
    exercise: 'Test',
    sets,
    reps,
    muscleGroup: 'LEGS',
    notes: null,
  });

  it('sums exercises, sets and total reps', () => {
    expect(planStats([workout(5, 5), workout(3, 10)])).toEqual({ exercises: 2, sets: 8, reps: 55 });
  });

  it('handles an empty plan', () => {
    expect(planStats([])).toEqual({ exercises: 0, sets: 0, reps: 0 });
  });
});
