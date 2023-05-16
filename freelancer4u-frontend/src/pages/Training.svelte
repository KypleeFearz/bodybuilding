<script>
    import axios from "axios";

    const api_root = window.location.origin;

    let trainings = [];
    let training = {
        id: null,
        ubung: null,
        satz: null,
        wiederholung: null,
    };

    function getTrainings() {
        var config = {
            method: "get",
            url: api_root + "/api/training",
            headers: {},
        };

        axios(config)
            .then(function (response) {
                trainings = response.data;
            })
            .catch(function (error) {
                alert("Could not get trainings");
                console.log(error);
            });
    }
    getTrainings();

    function createTraining() {
        var config = {
            method: "post",
            url: api_root + "/api/training",
            headers: {
                "Content-Type": "application/json",
            },
            data: training,
        };

        axios(config)
            .then(function (response) {
                alert("Training created");
                getTrainings();
            })
            .catch(function (error) {
                alert("Could not create Training");
                console.log(error);
            });
    }
</script>


<h1 class="mt-3">Create Training</h1>
<form class="mb-5">
    <div class="row mb-3">
        <div class="col">
            <label class="form-label" for="ubung">Ubung</label>
            <input
                bind:value={training.ubung}
                class="form-control"
                id="ubung"
                type="text"
            />
        </div>
    </div>
    <div class="row mb-3">
        <div class="col">
            <label class="form-label" for="satz">Satz</label>
            <input
                bind:value={training.satz}
                class="form-control"
                id="satz"
                type="number"
            />
        </div>
    </div>
    <div class="row mb-3">
        <div class="col">
            <label class="form-label" for="wiederholung">Wiederholung</label>
            <input
                bind:value={training.wiederholung}
                class="form-control"
                id="wiederholung"
                type="age"
            />
        </div>
    </div>
    <button type="button" class="btn btn-primary" on:click={createTraining}>Submit</button>
</form>

<h1>All Trainings</h1>
<table class="table">
    <thead>
        <tr>
            <th scope="col">Ubung</th>
            <th scope="col">Satz</th>
            <th scope="col">Wiederholung</th>
        </tr>
    </thead>
    <tbody>
        {#each trainings as training}
            <tr>
                <td>{training.ubung}</td>
                <td>{training.satz}</td>
                <td>{training.wiederholung}</td>
            </tr>
        {/each}
    </tbody>
</table>