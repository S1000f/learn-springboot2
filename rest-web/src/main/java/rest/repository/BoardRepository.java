package rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rest.domain.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
