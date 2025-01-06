@import com.github.alexeylapin.whaleone.domain.model.Deployment
@import java.util.List
@import java.time.format.DateTimeFormatter

@param List<Deployment> deployments
@param DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

@template.layout.bulma(content = @`
    <a href="/deployments/new" class="button">
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
                    <a href="/deployments/${deployment.id()}" class="button is-light is-small">edit</a>
                </td>
            </tr>
        @endfor
        </tbody>
    </table>
`)