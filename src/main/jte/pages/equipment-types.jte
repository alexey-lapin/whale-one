@import com.github.alexeylapin.whaleone.domain.model.EquipmentType
@import com.github.alexeylapin.whaleone.domain.repo.Page
@import java.time.format.DateTimeFormatter
@import org.springframework.boot.info.BuildProperties

@param Page<EquipmentType> equipmentTypes
@param DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
@param BuildProperties buildProperties

@template.layout.bulma(buildProperties = buildProperties, content = @`
    <a href="/equipment/types/new" class="button">
        <span class="icon">
            <i class="pi pi-plus"></i>
        </span>
        <span>New</span>
    </a>
    <table class="table is-hoverable is-fullwidth is-hidden-mobile">
        <thead>
        <tr>
            <th style="width: 5%">Id</th>
            <th style="width: 60%">Name</th>
            <th style="width: 15%">Created At</th>
            <th style="width: 15%">Created By</th>
            <th style="width: 5%">Actions</th>
        </tr>
        </thead>
        <tbody>
        @for (EquipmentType equipmentType : equipmentTypes.getContent())
            <tr>
                <td>${equipmentType.id()}</td>
                <td>${equipmentType.name()}</td>
                <td>${dateTimeFormatter.format(equipmentType.createdAt())}</td>
                <td>${equipmentType.createdBy()}</td>
                <td>
                    <a href="/equipment/types/${equipmentType.id()}" class="button is-small">edit</a>
                </td>
            </tr>
        @endfor
        </tbody>
    </table>
    <div class="is-hidden-tablet mt-3">
        @for (EquipmentType equipmentType : equipmentTypes.getContent())
            <div class="card">
                <header class="card-header">
                    <p class="card-header-title"># ${equipmentType.id()} ${equipmentType.name()}</p>
                    <a class="card-header-icon"
                       href="/equipment/types/${equipmentType.id()}"
                       aria-label="more options">
                        <span class="icon">
                            <i class="pi pi-pen-to-square" aria-hidden="true"></i>
                        </span>
                    </a>
                </header>
                <div class="card-content">
                    <div class="content">
                        <p>Created At: <b>${dateTimeFormatter.format(equipmentType.createdAt())}</b></p>
                        <p>Created By: <b>${equipmentType.createdBy()}</b></p>
                    </div>
                </div>
            </div>
        @endfor
    </div>
    @template.partials.pagination(page = equipmentTypes, pageUrl = "/equipment/types?page=")
`)