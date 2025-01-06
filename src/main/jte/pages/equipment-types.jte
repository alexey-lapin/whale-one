@import com.github.alexeylapin.whaleone.domain.model.EquipmentType
@import java.util.List

@param List<EquipmentType> equipmentTypes

@template.layout.bulma(content = @`
    <a href="/equipment/types/new" class="button">
        <span class="icon">
            <i class="pi pi-plus"></i>
        </span>
        <span>New</span>
    </a>
    <table class="table is-hoverable is-fullwidth">
        <thead>
        <tr>
            <th>Id</th>
            <th>Name</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        @for (EquipmentType equipmentType : equipmentTypes)
            <tr>
                <td>${equipmentType.id()}</td>
                <td>${equipmentType.name()}</td>
                <td>
                    <a href="/equipment/types/${equipmentType.id()}" class="button is-light is-small">edit</a>
                </td>
            </tr>
        @endfor
        </tbody>
    </table>
`)