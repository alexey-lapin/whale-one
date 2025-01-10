@import com.github.alexeylapin.whaleone.domain.model.Deployment
@import java.util.List
@import java.time.format.DateTimeFormatter
@import org.springframework.boot.info.BuildProperties

@param List<Deployment> deployments
@param DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
@param BuildProperties buildProperties

@template.layout.bulma(buildProperties = buildProperties, content = @`
    <a href="/deployments/new" class="button">
            <span class="icon">
                <i class="pi pi-plus"></i>
            </span>
        <span>New</span>
    </a>
    <table class="table is-hoverable is-fullwidth is-hidden-mobile">
        <thead>
        <tr>
            <th>Id</th>
            <th>Name</th>
            <th>Description</th>
            <th>Status</th>
            <th>Created At</th>
            <th>Created By</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        @for (Deployment deployment : deployments)
            <tr>
                <td>${deployment.id()}</td>
                <td>${deployment.name()}</td>
                <td>${deployment.description()}</td>
                <td>${deployment.status()}</td>
                <td>${dateTimeFormatter.format(deployment.createdAt())}</td>
                <td>${deployment.createdBy()}</td>
                <td>
                    <a href="/deployments/${deployment.id()}" class="button is-small">edit</a>
                </td>
            </tr>
        @endfor
        </tbody>
    </table>
    <div class="is-hidden-tablet mt-3">
        @for (Deployment deployment : deployments)
            <div class="card">
                <header class="card-header">
                    <p class="card-header-title"># ${deployment.id()} ${deployment.name()}</p>
                    <a class="card-header-icon"
                       href="/deployments/${deployment.id()}"
                       aria-label="more options">
                        <span class="icon">
                            <i class="pi pi-pen-to-square" aria-hidden="true"></i>
                        </span>
                    </a>
                </header>
                <div class="card-content">
                    <div class="content">
                        <p>Desc: <b>${deployment.description()}</b></p>
                        <p>Status: <b>${deployment.status()}</b></p>
                        <p>Created At: <b>${dateTimeFormatter.format(deployment.createdAt())}</b></p>
                        <p>Created By: <b>${deployment.createdBy()}</b></p>
                    </div>
                </div>
            </div>
        @endfor
    </div>
`)