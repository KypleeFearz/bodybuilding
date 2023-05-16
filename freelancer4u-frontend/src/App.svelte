<script>
	import Router from "svelte-spa-router";
	import routes from "./routes";
</script>

<div id="app">
	<nav class="navbar navbar-expand-lg bg-light">
		<div class="container-fluid">
			<a class="navbar-brand" href="#/">Freelancer4U</a>
			<button
				class="navbar-toggler"
				type="button"
				data-bs-toggle="collapse"
				data-bs-target="#navbarNav"
				aria-controls="navbarNav"
				aria-expanded="false"
				aria-label="Toggle navigation"
			>
				<span class="navbar-toggler-icon" />
			</button>
			<div class="collapse navbar-collapse" id="navbarNav">
				<ul class="navbar-nav">
					<li class="nav-item">
						<a
							class="nav-link"
							href="#/users">Users</a
						>
					</li>
					<li class="nav-item">
						<a
							class="nav-link"
							href="#/forum">Forum</a
						>
					</li>
					<li class="nav-item">
						<a
							class="nav-link"
							href="#/training">Training</a
						>
					</li>
					<li class="nav-item">
						<a
							class="nav-link"
							href="#/home">Home</a
						>
					</li>
				</ul>
			</div>
		</div>
	</nav>

	<div class="container">
		<Router {routes} />
	</div>
</div>