package finley.peter.nextgear.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import finley.peter.nextgear.model.Contract;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

}
