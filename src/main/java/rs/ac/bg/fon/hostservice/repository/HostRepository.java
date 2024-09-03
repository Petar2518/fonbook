package rs.ac.bg.fon.hostservice.repository;

import rs.ac.bg.fon.hostservice.model.Host;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface HostRepository extends JpaRepository<Host, Long> {

}
