package econovation.springbootneo4j;

import econovation.springbootneo4j.account.Account;
import econovation.springbootneo4j.account.AccountRepository;
import econovation.springbootneo4j.account.Role;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class Neo4jRunner implements ApplicationRunner {

    @Autowired
    AccountRepository accountRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Account account = new Account();    // account 객체 생성
        account.setEmail("aaaa@mail.com");
        account.setUsername("aaaa");

        Role role = new Role();             // Role 객체 생성
        role.setName("user");

        account.getRoles().add(role);   // account 객체에 role을 등록

        accountRepository.save(account);

        System.out.println("finished");
    }
}
