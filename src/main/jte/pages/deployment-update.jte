@import com.github.alexeylapin.whaleone.domain.model.Deployment
@import com.github.alexeylapin.whaleone.domain.model.DeploymentEquipment
@import com.github.alexeylapin.whaleone.domain.model.DeploymentStatus
@import com.github.alexeylapin.whaleone.domain.model.Equipment
@import java.time.format.DateTimeFormatter
@import java.util.List
@import java.util.Map
@import org.springframework.boot.info.BuildProperties

@param Deployment deployment
@param List<DeploymentEquipment> deploymentEquipmentList
@param Map<Long, Equipment> equipment
@param boolean isEquipmentEditable
@param DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
@param BuildProperties buildProperties

@template.layout.bulma(buildProperties = buildProperties, content = @`
    <form action="/deployments/${deployment.id()}" method="post">
        <ul class="steps is-narrow is-small is-centered has-content-centered">
            @for(DeploymentStatus status : DeploymentStatus.values())
                <li class="steps-segment ${deployment.status() == status ? "is-active" : ""} ${status.isTerminal() ? "is-dashed" : ""}">
                    <span class="steps-marker">
                        @if(deployment.status().isTerminal() && deployment.status() == status)
                            <span class="icon">
                                <i class="pi pi-check"></i>
                            </span>
                        @endif
                    </span>
                    <div class="steps-content">
                        <p>${status}</p>
                    </div>
                </li>
            @endfor
        </ul>
        <div class="columns">
            <div class="column">
                <div class="field">
                    <label class="label">Id</label>
                    <div class="control">
                        <input class="input"
                               type="text"
                               name="id"
                               value="${deployment.id()}"
                               disabled
                        >
                    </div>
                </div>
            </div>
            <div class="column">
                <div class="field">
                    <label class="label">Created At</label>
                    <div class="control">
                        <input class="input"
                               type="text"
                               name="createdAt"
                               value="${dateTimeFormatter.format(deployment.createdAt())}"
                               disabled
                        >
                    </div>
                </div>
            </div>
            <div class="column">
                <div class="field">
                    <label class="label">Created By</label>
                    <div class="control">
                        <input class="input"
                               type="text"
                               name="createdBy"
                               value="${deployment.createdBy()}"
                               disabled
                        >
                    </div>
                </div>
                <div class="field is-hidden">
                    <div class="control">
                        <input class="input"
                               type="text"
                               name="createdById"
                               value="${deployment.createdById()}"
                        >
                    </div>
                </div>
            </div>
        </div>
        <div class="field">
            <label class="label">Name</label>
            <div class="control">
                <input class="input"
                       type="text"
                       name="name"
                       placeholder="Name"
                       value="${deployment.name()}"
                       required
                >
            </div>
        </div>
        <div class="field">
            <label class="label">Description</label>
            <div class="control">
                <input class="input"
                       type="text"
                       name="description"
                       placeholder="Description"
                       value="${deployment.description()}"
                >
            </div>
        </div>

        <div class="field is-grouped is-grouped-multiline">
            @if(deployment.status() == DeploymentStatus.NEW)
                <div class="control">
                    <button class="button is-link"
                            formaction="/deployments/${deployment.id()}?status=${DeploymentStatus.ASSIGN}">
                        Assign
                    </button>
                </div>
                <div class="control">
                    <button class="button is-link is-light">Save</button>
                </div>
            @elseif(deployment.status() == DeploymentStatus.ASSIGN)
                <div class="control">
                    <button class="button is-link"
                            formaction="/deployments/${deployment.id()}?status=${DeploymentStatus.DEPLOYED}">
                        Deploy
                    </button>
                </div>
                <div class="control">
                    <button class="button is-link is-light">Save</button>
                </div>
            @elseif(deployment.status() == DeploymentStatus.DEPLOYED)
                <div class="control">
                    <button class="button is-link"
                            formaction="/deployments/${deployment.id()}?status=${DeploymentStatus.RECOVERED}">
                        Recover
                    </button>
                </div>
                <div class="control">
                    <button class="button is-link is-light">Save</button>
                </div>
            @else
                <div class="control">
                    <button class="button is-link">Save</button>
                </div>
            @endif
            <a href="/deployments/${deployment.id()}/metadata" class="button is-light">Metadata</a>
            <a href="/deployments" class="button is-light">Back</a>
        </div>
    </form>
    <div class="my-4">
        @template.partials.deployment-equipment(deployment = deployment,
        deploymentEquipmentList = deploymentEquipmentList,
        equipmentMap = equipment,
        isEquipmentEditable = isEquipmentEditable)
    </div>
`)