package mate.academy.intro.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

@NoRepositoryBean
public interface PagingAndSortingRepository<T, IdT> extends Repository<T, IdT> {
    Iterable<T> findAll(Sort sort);

    Page<T> findAll(Pageable pageable);
}
