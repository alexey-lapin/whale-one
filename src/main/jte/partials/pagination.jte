@param com.github.alexeylapin.whaleone.domain.repo.Page<?> page
@param String pageUrl

@if(page.getTotalPages() > 1)
    <nav class="pagination is-centered is-small" role="navigation" aria-label="pagination">
        @if(page.hasPrevious())
            <a href="${pageUrl}${page.getNumber() - 1}" class="pagination-previous">Previous</a>
        @else
            <a class="pagination-previous is-disabled">Previous</a>
        @endif
        @if(page.hasNext())
            <a href="${pageUrl}${page.getNumber() + 1}" class="pagination-next">Next</a>
        @else
            <a class="pagination-next is-disabled">Next</a>
        @endif
        <ul class="pagination-list">
            @for(int i = 1; i <= page.getTotalPages(); i++)
                <li>
                    <a href="${pageUrl}${i - 1}"
                       class="pagination-link ${page.getNumber() == i - 1 ? "is-current" : ""}"
                       aria-label="Goto page ${i}">${i}</a>
                </li>
            @endfor
        </ul>
    </nav>
@endif