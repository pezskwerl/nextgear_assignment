package finley.peter.nextgear.web.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import finley.peter.nextgear.model.Contract;
import finley.peter.nextgear.model.ContractStatus;
import finley.peter.nextgear.model.ContractType;

/**
 * Integration test that demonstrates/tests various uses of the
 * {@link ContractController} RESTful API.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class ContractControllerIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;
	
	private static final ParameterizedTypeReference<List<Contract>> CONTRACT_LIST_PARAMETERIZED_TYPE_REF = 
			new ParameterizedTypeReference<List<Contract>>() {};
	
	@Test
	public void testAll() {
		
		// add some contracts
		Contract contract1 = createContract("contract1", 1, ContractType.EXPRESS, 1000);
		Contract contract2 = createContract("contract2", 1, ContractType.EXPRESS, 1000);
		Contract contract3 = createContract("contract3", 1, ContractType.SALES, 200000);
		
		// get approved contracts
		List<Contract> contracts = getContracts(ContractStatus.APPROVED);
		
		assertThat(contracts).containsExactlyInAnyOrder(contract1, contract2);
		
		// update a contract
		String newName = "contract3 updated";
		contract3.setName(newName);
		
		updateContract(contract3);
		
		// confirm name updated (get individual contract)
		contract3 = getContract(contract3.getId());
		assertThat(contract3.getName()).isEqualTo(newName);
		
		// delete a contract
		deleteContract(contract2.getId());
		
		// get all contracts
		contracts = getContracts();
		
		assertThat(contracts).containsExactlyInAnyOrder(contract1, contract3);
	}
	
	/**
	 * Test that a 400 error is returned when attempting to create a contract with
	 * EXPRESS type with an amount over the limit.
	 */
	@Test
	public void testExpressContractAmountError() {
		
		Contract contract = new Contract();
		contract.setName("name");
		contract.setBusinessNumber(1);
		contract.setType(ContractType.EXPRESS);
		contract.setAmountRequested(50000);
		
		ResponseEntity<ErrorInformation> response = restTemplate.postForEntity("/contracts", contract, ErrorInformation.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			
		ErrorInformation errorInfo = response.getBody();
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(errorInfo.getMessage()).isEqualTo("EXPRESS contract amounts must be less than 50000");
		assertThat(errorInfo.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(errorInfo.getError()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
	}
	
	/**
	 * Test that a 404 error is returned when attempting to find a contract that
	 * doesn't exist.
	 */
	@Test
	public void testContractNotFound() {

		ResponseEntity<ErrorInformation> response = restTemplate.getForEntity("/contracts/{0}", ErrorInformation.class, 999);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		
		ErrorInformation errorInfo = response.getBody();
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(errorInfo.getMessage()).isEqualTo("Contract does not exist.");
		assertThat(errorInfo.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
		assertThat(errorInfo.getError()).isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase());
	}
	
	private Contract createContract(String name, long businessNumber, ContractType type, int amountRequested) {
		
		Contract contract = new Contract();
		contract.setName(name);
		contract.setBusinessNumber(businessNumber);
		contract.setType(type);
		contract.setAmountRequested(amountRequested);
		
		ResponseEntity<Contract> response = restTemplate.postForEntity("/contracts", contract, Contract.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		
		return response.getBody();
	}

	private List<Contract> getContracts(ContractStatus status) {
		
		ResponseEntity<List<Contract>> response = 
				restTemplate.exchange("/contracts?status={0}", HttpMethod.GET, null, CONTRACT_LIST_PARAMETERIZED_TYPE_REF, status);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		return response.getBody();
	}
	
	private List<Contract> getContracts() {
		
		return getContracts(null);
	}
	
	private Contract getContract(long id) {

		ResponseEntity<Contract> response = restTemplate.getForEntity("/contracts/{0}", Contract.class, id);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		return response.getBody();
	}
	
	private void updateContract(Contract contract) {
		
		restTemplate.put("/contracts/{0}", contract, contract.getId());
	}
	
	private void deleteContract(long id) {
		
		restTemplate.delete("/contracts/{0}", id);
	}
}
