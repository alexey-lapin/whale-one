@import com.github.alexeylapin.whaleone.domain.model.Deployment
@import com.github.alexeylapin.whaleone.domain.model.DeploymentEquipment
@import com.github.alexeylapin.whaleone.domain.model.Equipment
@import java.util.List
@import java.util.Map

@param Deployment deployment
@param List<DeploymentEquipment> deploymentEquipmentList
@param Map<Long, Equipment> equipmentMap
@param boolean isEquipmentEditable

<div id="equipment" class="card">
    <div class="card-content">
        <div class="content">
            <p class="is-size-5">🛠️ Equipment</p>
            <fieldset disabled="${!isEquipmentEditable}">
                @for(var deploymentEquipment : deploymentEquipmentList)
                    <div class="field is-grouped">
                        <div class="control">
                            <input class="input"
                                   type="text"
                                   name="id"
                                   value="${deploymentEquipment.equipmentId()}"
                                   readonly
                            >
                        </div>
                        <div class="control">
                            <input class="input"
                                   type="text"
                                   name="name"
                                   value="${equipmentMap.get(deploymentEquipment.equipmentId()).name()}"
                                   readonly
                            >
                        </div>
                        <a href="/equipment/${deploymentEquipment.equipmentId()}"
                           class="button is-white"
                           style="cursor: pointer"
                        >
                            <i class="pi pi-external-link"></i>
                        </a>
<%--                        <div class="control">--%>
                            <button class="button is-danger is-outlined"
                                    hx-delete="/deployments/${deployment.id()}/equipment/${deploymentEquipment.equipmentId()}"
                                    hx-target="#equipment"
                                    hx-swap="outerHTML">
                                <i class="pi pi-trash"></i>
                            </button>
<%--                        </div>--%>
                    </div>
                @endfor
            </fieldset>
            <form action="/deployments/${deployment.id()}/equipment" method="post" class="mt-2">
                <fieldset disabled="${!isEquipmentEditable}">
                    <div class="field is-grouped">
                        <div class="control">
                            <div class="select">
                                <select name="equipment-id" required>
                                    <option></option>
                                    @for(var equipment : equipmentMap.values().stream().filter(e -> e.deploymentId() == null).toList()))
                                        <option value="${equipment.id()}">${equipment.name()}</option>
                                    @endfor
                                </select>
                            </div>
                        </div>
                        <div class="control">
                            <button class="button is-link">Add</button>
                        </div>
                    </div>
                </fieldset>
            </form>
        </div>
    </div>
</div>