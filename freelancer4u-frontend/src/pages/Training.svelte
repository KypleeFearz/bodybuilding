<script>
    import axios from "axios";
    import { jwt_token, user } from "../store";

    const api_root = window.location.origin;

    let trainings = [];
    let training = {
        id: null,
        ubung: null,
        satz: null,
        wiederholung: null,
        fokus: null,
    };
    let currentUser = { plaene: null };
    let fokus = null;

    function getTrainings() {
        var config = {
            method: "get",
            url: api_root + "/api/training",
            headers: { Authorization: "Bearer " + $jwt_token },
        };

        axios(config)
            .then(function (response) {
                trainings = response.data;
                getUser();
            })
            .catch(function (error) {
                alert("Could not get trainings");
                console.log(error);
            });
    }

    getTrainings();

    function getTrainingsByFokus() {
        if (fokus !== null && fokus !== "") {
            var config = {
                method: "get",
                url: api_root + "/api/training/fokus?fokus=" + fokus,
                headers: { Authorization: "Bearer " + $jwt_token },
            };

            axios(config)
                .then(function (response) {
                    trainings = response.data;
                })
                .catch(function (error) {
                    alert("Could not get trainings");
                    console.log(error);
                });
        }else{
            getTrainings();
        }
    }

    function getUser() {
        var config = {
            method: "get",
            url: api_root + "/api/user/name/" + $user.nickname,
            headers: { Authorization: "Bearer " + $jwt_token },
        };

        axios(config)
            .then(function (response) {
                currentUser = response.data;
            })
            .catch(function (error) {
                alert("Could not get trainings");
                console.log(error);
            });
    }

    function createTraining() {
        var config = {
            method: "post",
            url: api_root + "/api/training",
            headers: {
                "Content-Type": "application/json",
                Authorization: "Bearer " + $jwt_token,
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
    function buyTraining(trainingId) {
        let data = { userName: $user.nickname, trainingId: trainingId };
        var config = {
            method: "put",
            url: api_root + "/api/service/me/buyTraining",
            headers: {
                "Content-Type": "application/json",
                Authorization: "Bearer " + $jwt_token,
            },
            data: data,
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

{#if $user.user_roles && $user.user_roles.includes("admin")}

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
                type="number"
            />
        </div>
    </div>
    <div class="row mb-3">
        <div class="col">
            <label class="form-label" for="fokus">Fokus</label>
            <input
                bind:value={training.fokus}
                class="form-control"
                id="fokus"
                type="text"
            />
        </div>
    </div>
    <button type="button" class="btn btn-primary" on:click={createTraining}
        >Submit</button
    >
</form>
{/if}
<h1>All Trainings</h1>
<div class="col-auto">
    <label for="" class="col-form-label">Fokus: </label>
</div>
<div class="col-3">
    <select bind:value={fokus} class="form-select" id="typefilter" type="text">
        <option value="ALL" />
        <option value="CHEST">CHEST</option>
        <option value="BACK">BACK</option>
        <option value="LEGS">LEGS</option>
        <option value="ARMS">ARMS</option>
    </select>
</div>
<div class="col-3">
    <button
        id="filter"
        type="button"
        class="btn btn-primary"
        on:click={getTrainingsByFokus}>filter</button
    >
</div>
<table class="table">
    <thead>
        <tr>
            <th scope="col">Fokus</th>
            <th scope="col">Ubung</th>
            <th scope="col">Satz</th>
            <th scope="col">Wiederholung</th>
            <th scope="col">Actions</th>
        </tr>
    </thead>
    <tbody>
        {#each trainings as training}
            <tr>
                <td>{training.fokus}</td>
                {#if currentUser.plaene != null && currentUser.plaene.some((plan) => plan["ubung"] === training.ubung)}
                <td>{training.ubung}</td>
                <td>{training.satz}</td>
                <td>{training.wiederholung}</td>
                {/if}
                <td>
                    {#if currentUser.plaene != null && !currentUser.plaene.some((plan) => plan["ubung"] === training.ubung)}
                        <button
                            type="button"
                            class="btn btn-primary btn-sm"
                            on:click={() => {
                                buyTraining(training.id);
                            }}
                        >
                            Purchase
                        </button>
                    {/if}
                </td>
            </tr>
        {/each}
    </tbody>
</table>
