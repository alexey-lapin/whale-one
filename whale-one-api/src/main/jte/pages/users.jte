@import com.github.alexeylapin.whaleone.domain.model.User
@import com.github.alexeylapin.whaleone.domain.repo.Page
@import org.springframework.boot.info.BuildProperties

@param Page<User> users
@param BuildProperties buildProperties

@template.layout.bulma-container(buildProperties = buildProperties, content = @`
    <a href="/users/new" class="button">
            <span class="icon">
                <i class="pi pi-plus"></i>
            </span>
        <span>New</span>
    </a>
    <table class="table is-hoverable is-fullwidth is-hidden-mobile">
        <thead>
        <tr>
            <th style="width: 5%">Id</th>
            <th style="width: 80%">Name</th>
            <th style="width: 10%">Is Enabled</th>
            <th style="width: 5%">Actions</th>
        </tr>
        </thead>
        <tbody>
        @for (var user : users.getContent())
            <tr>
                <td>${user.id()}</td>
                <td>${user.username()}</td>
                <td>${user.enabled()}</td>
                <td>
                    <a href="/users/${user.id()}" class="button is-small">edit</a>
                </td>
            </tr>
        @endfor
        </tbody>
    </table>
    <div class="is-hidden-tablet my-3">
        @for (User user : users.getContent())
            <div class="card">
                <header class="card-header">
                    <p class="card-header-title"># ${user.id()} ${user.username()}</p>
                    <a class="card-header-icon"
                       href="/users/${user.id()}"
                       aria-label="more options">
                        <span class="icon">
                            <i class="pi pi-pen-to-square" aria-hidden="true"></i>
                        </span>
                    </a>
                </header>
                <div class="card-content">
                    <div class="content">
                        <p>Is Enabled: <b>${user.enabled()}</b></p>
                    </div>
                </div>
            </div>
        @endfor
    </div>
    @template.partials.pagination(page = users, pageUrl = "/users?page=")
`)