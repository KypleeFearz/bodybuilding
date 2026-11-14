<script lang="ts">
  import { onMount } from 'svelte';
  import { api } from '../lib/api';
  import { MUSCLE_GROUPS } from '../lib/types';

  let workoutCount = $state<number | null>(null);
  let threadCount = $state<number | null>(null);

  onMount(async () => {
    try {
      const [workouts, threads] = await Promise.all([api.workouts(), api.threads()]);
      workoutCount = workouts.length;
      threadCount = threads.length;
    } catch {
      // stats are optional, the App shows a banner when the API is down
    }
  });

  const features = [
    {
      title: 'Workout library',
      text: 'Browse exercises with sets, reps and coaching notes. Filter by muscle group in one click.',
      href: '#/workouts',
    },
    {
      title: 'Personal plan',
      text: 'Save the exercises you want to train. You get a confirmation mail for every addition.',
      href: '#/plan',
    },
    {
      title: 'Community forum',
      text: 'Ask about splits, deloads and technique. Authors edit their posts, admins keep it clean.',
      href: '#/forum',
    },
  ];
</script>

<section class="hero">
  <p class="eyebrow">Train with a plan</p>
  <h1>Build your program.<br /><span class="accent">Share the grind.</span></h1>
  <p class="lead">
    A workout library, a personal training plan and a forum for everyone who lifts. Pick exercises, track your
    volume and talk shop with the community.
  </p>
  <div class="row">
    <a class="btn btn-primary" href="#/workouts">Browse workouts</a>
    <a class="btn btn-ghost" href="#/forum">Join the forum</a>
  </div>

  <dl class="stats">
    <div>
      <dt>Workouts</dt>
      <dd>{workoutCount ?? '–'}</dd>
    </div>
    <div>
      <dt>Muscle groups</dt>
      <dd>{MUSCLE_GROUPS.length}</dd>
    </div>
    <div>
      <dt>Forum threads</dt>
      <dd>{threadCount ?? '–'}</dd>
    </div>
  </dl>
</section>

<section class="grid features">
  {#each features as feature (feature.title)}
    <a class="card card-interactive feature" href={feature.href}>
      <h3>{feature.title}</h3>
      <p class="muted">{feature.text}</p>
      <span class="more">Open →</span>
    </a>
  {/each}
</section>

<style>
  .hero {
    padding-block: 4.5rem 3rem;
    display: grid;
    gap: 1.4rem;
    max-width: 760px;
  }

  h1 {
    font-size: clamp(2.4rem, 6vw, 4rem);
    font-weight: 850;
    letter-spacing: -0.035em;
  }

  .accent {
    color: var(--accent);
  }

  .lead {
    font-size: 1.15rem;
    color: var(--muted);
    max-width: 600px;
  }

  .stats {
    display: flex;
    gap: 2.5rem;
    margin: 1rem 0 0;
    flex-wrap: wrap;
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
    font-size: 2rem;
    font-weight: 800;
  }

  .features {
    padding-bottom: 4rem;
  }

  .feature {
    display: grid;
    gap: 0.6rem;
  }

  .more {
    color: var(--accent);
    font-weight: 600;
    font-size: 0.9rem;
    margin-top: 0.4rem;
  }
</style>
