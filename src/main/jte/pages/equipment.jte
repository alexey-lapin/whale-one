@import com.github.alexeylapin.whaleone.domain.model.Equipment
@import com.github.alexeylapin.whaleone.domain.model.EquipmentType
@import java.util.List
@import java.util.Map
@import org.springframework.boot.info.BuildProperties

@param List<Equipment> equipmentList
@param Map<Integer, EquipmentType> equipmentTypes
@param BuildProperties buildProperties

@template.layout.bulma(buildProperties = buildProperties, content = @`
    <a href="/equipment/new">
        <button class="button">
            <span class="icon">
                <i class="pi pi-plus"></i>
            </span>
            <span>New</span>
        </button>
    </a>
    <a href="/equipment/types">
        <button class="button">
            <span class="icon">
                <i class="pi pi-cog"></i>
            </span>
            <span>Setup</span>
        </button>
    </a>
    <table class="table is-hoverable is-fullwidth is-hidden-mobile">
        <thead>
        <tr>
            <th>Id</th>
            <th>Name</th>
            <th>Type</th>
            <th>Assigned For</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        @for (Equipment equipment : equipmentList)
            <tr>
                <td>${equipment.id()}</td>
                <td>${equipment.name()}</td>
                <td>${equipmentTypes.get(equipment.type()).name()}</td>
                <td>
                    @if(equipment.deploymentId() != null)
                        <div class="field has-addons">
                            <p class="control">
                                <a class="button is-small is-static">
                                    # ${equipment.deploymentId()}
                                </a>
                            </p>
                            <a href="/deployments/${equipment.deploymentId()}" class="button is-small">
                            <span class="icon">
                                <i class="pi pi-external-link"></i>
                            </span>
                            </a>
                        </div>
                    @endif
                </td>
                <td>
                    <a href="/equipment/${equipment.id()}" class="button is-small">edit</a>
                </td>
            </tr>
        @endfor
        </tbody>
    </table>
    <div class="is-hidden-tablet mt-3">
        @for (Equipment equipment : equipmentList)
            <div class="card">
                <header class="card-header">
                    <p class="card-header-title"># ${equipment.id()} ${equipment.name()}</p>
                    <a class="card-header-icon"
                       href="/equipment/${equipment.id()}"
                       aria-label="more options">
                        <span class="icon">
                            <i class="pi pi-pen-to-square" aria-hidden="true"></i>
                        </span>
                    </a>
                </header>
                <div class="card-content">
                    <div class="content">
                        <p>Type: <b>${equipmentTypes.get(equipment.type()).name()}</b></p>
                        @if(equipment.deploymentId() != null)
                            <p>Assigned For: <b># ${equipment.deploymentId()}</b></p>
                        @endif
                    </div>
                </div>
            </div>
        @endfor
    </div>
`)