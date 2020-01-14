package econovation.springbootjpa.Account;

import econovation.springbootjpa.account.Account;
import econovation.springbootjpa.account.AccountRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

//@SpringBootTest 는 애플리케이션에 존재하는 모든 Bean이 다 등록됩니다. 이런경우 DB 서버는 POSTGRESQL이 사용된다.
//@SpringBootTest(properties = "DBURL")  overriding 시켜준다.
@RunWith(SpringRunner.class)
@DataJpaTest    // Repository와 관련된 빈들만 등록을해서 테스트를 만드는 것, 인메모리 데이터베이스가 무조건 필요하다.(자동으로 설정된다)
public class AccountRepositoryTest {

    @Autowired
    DataSource dataSource;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    AccountRepository accountRepository;

    @Test
    public void di() throws SQLException {
        try(Connection connection = dataSource.getConnection()){
            Account account = new Account();
            account.setUsername("keesun");
            account.setPassword("pass");

            Account newAccount = accountRepository.save(account);

            assertThat(newAccount).isNotNull();

            Optional<Account> existingAccount = accountRepository.findByUsername(newAccount.getUsername("keesun"));
            assertThat(existingAccount).isNotEmpty();

            Optional<Account> nonexistingAccount = accountRepository.findByUsername("whiteship");
            assertThat(nonexistingAccount).isEmpty();

        }
    }
}
