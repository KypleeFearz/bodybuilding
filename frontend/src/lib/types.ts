export const MUSCLE_GROUPS = ['CHEST', 'BACK', 'LEGS', 'SHOULDERS', 'ARMS', 'CORE', 'FULL_BODY'] as const;

export type MuscleGroup = (typeof MUSCLE_GROUPS)[number];

export interface Workout {
  id: string;
  exercise: string;
  sets: number;
  reps: number;
  muscleGroup: MuscleGroup;
  notes: string | null;
}

export interface WorkoutInput {
  exercise: string;
  sets: number;
  reps: number;
  muscleGroup: MuscleGroup;
  notes?: string;
}

export interface Me {
  id: string;
  username: string;
  email: string | null;
  admin: boolean;
  savedWorkoutIds: string[];
}

export interface ThreadSummary {
  id: string;
  title: string;
  authorName: string;
  createdAt: string;
  lastActivityAt: string;
  replies: number;
}

export interface Post {
  id: string;
  authorId: string;
  authorName: string;
  content: string;
  createdAt: string;
  editedAt: string | null;
}

export interface Thread {
  id: string;
  title: string;
  authorId: string;
  authorName: string;
  createdAt: string;
  posts: Post[];
}

export interface UserProfile {
  id: string;
  username: string;
  email: string | null;
  savedWorkouts: number;
  createdAt: string;
}

export interface ClientConfig {
  authEnabled: boolean;
  authDomain: string;
  authClientId: string;
  authAudience: string;
}
