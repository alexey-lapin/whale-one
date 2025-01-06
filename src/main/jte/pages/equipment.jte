@import com.github.alexeylapin.whaleone.domain.model.Equipment
@import com.github.alexeylapin.whaleone.domain.model.EquipmentType
@import java.util.List
@import java.util.Map

@param List<Equipment> equipmentList
@param Map<Integer, EquipmentType> equipmentTypes

@template.layout.bulma(content = @`
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
    <table class="table is-hoverable is-fullwidth">
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
                <td>${equipment.deploymentId()}
                    @if(equipment.deploymentId() != null)
                        <a href="/deployments/${equipment.deploymentId()}" class="button is-white is-small">
                            <i class="pi pi-external-link"></i>
                        </a>
                    @endif
                </td>
                <td>
                    <a href="/equipment/${equipment.id()}" class="button is-light is-small">edit</a>
                </td>
            </tr>
        @endfor
        </tbody>
    </table>
`)