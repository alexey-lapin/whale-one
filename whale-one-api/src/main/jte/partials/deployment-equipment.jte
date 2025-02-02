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
            @for(var deploymentEquipment : deploymentEquipmentList)
                <div class="field has-addons mb-0">
                    <p class="control">
                        <a class="button is-static">
                            # ${deploymentEquipment.equipmentId()}
                        </a>
                    </p>
                    <p class="control">
                        <input class="input"
                               type="text"
                               value="${equipmentMap.get(deploymentEquipment.equipmentId()).name()}"
                               readonly>
                    </p>
                    <p class="control">
                        <a href="/equipment/${deploymentEquipment.equipmentId()}"
                           class="button"
                           style="cursor: pointer"
                        >
                                <span class="icon">
                                    <i class="pi pi-external-link"></i>
                                </span>
                        </a>
                    </p>
                    @if(isEquipmentEditable)
                        <p class="control">
                            <button class="button has-text-danger"
                                    hx-delete="/deployments/${deployment.id()}/equipment/${deploymentEquipment.equipmentId()}"
                                    hx-target="#equipment"
                                    hx-swap="outerHTML">
                                <span class="icon">
                                    <i class="pi pi-trash"></i>
                                </span>
                            </button>
                        </p>
                    @endif
                </div>
            @endfor
            <div class="file">
                <label class="file-label">
                    <input class="file-input" type="file" name="resume"/>
                    <span class="file-cta">
                        <span class="file-icon">
                          <i class="pi pi-upload"></i>
                        </span>
                        <span class="file-label"> Choose a file… </span>
                    </span>
                </label>
            </div>
            @if(isEquipmentEditable)
                <form action="/deployments/${deployment.id()}/equipment" method="post" class="mt-2">
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
                </form>
            @endif
        </div>
    </div>
</div>
