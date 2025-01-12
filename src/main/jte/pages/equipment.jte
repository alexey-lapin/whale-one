@import com.github.alexeylapin.whaleone.domain.model.Equipment
@import com.github.alexeylapin.whaleone.domain.model.EquipmentType
@import com.github.alexeylapin.whaleone.domain.repo.Page
@import java.time.format.DateTimeFormatter
@import java.util.Map
@import org.springframework.boot.info.BuildProperties

@param Page<Equipment> equipmentList
@param Map<Long, EquipmentType> equipmentTypes
@param DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
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
            <th style="width: 5%">Id</th>
            <th style="width: 25%">Name</th>
            <th style="width: 25%">Type</th>
            <th style="width: 10%">Assigned For</th>
            <th style="width: 15%">Created At</th>
            <th style="width: 15%">Created By</th>
            <th style="width: 5%">Actions</th>
        </tr>
        </thead>
        <tbody>
        @for (Equipment equipment : equipmentList.getContent())
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
                <td>${dateTimeFormatter.format(equipment.createdAt())}</td>
                <td>${equipment.createdBy()}</td>
                <td>
                    <a href="/equipment/${equipment.id()}" class="button is-small">edit</a>
                </td>
            </tr>
        @endfor
        </tbody>
    </table>
    <div class="is-hidden-tablet my-3">
        @for (Equipment equipment : equipmentList.getContent())
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
                        <p>Created At: <b>${dateTimeFormatter.format(equipment.createdAt())}</b></p>
                        <p>Created By: <b>${equipment.createdBy()}</b></p>
                    </div>
                </div>
            </div>
        @endfor
    </div>
    @template.partials.pagination(page = equipmentList, pageUrl = "/equipment?page=")
`)