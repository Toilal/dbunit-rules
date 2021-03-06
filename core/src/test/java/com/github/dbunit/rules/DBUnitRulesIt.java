package com.github.dbunit.rules;

import com.github.dbunit.rules.api.dataset.DataSet;
import com.github.dbunit.rules.api.dataset.SeedStrategy;
import com.github.dbunit.rules.dataset.DataSetExecutorImpl;
import com.github.dbunit.rules.model.Follower;
import com.github.dbunit.rules.model.Tweet;
import com.github.dbunit.rules.model.User;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * Created by pestano on 23/07/15.
 */

@RunWith(JUnit4.class)
public class DBUnitRulesIt {

    @Rule
    public EntityManagerProvider emProvider = EntityManagerProvider.instance("rules-it");

    @Rule
    public DBUnitRule dbUnitRule = DBUnitRule.instance(emProvider.getConnection());


    @Test
    @DataSet(value = "datasets/yml/users.yml",disableConstraints = true)
    public void shouldSeedDataSetDisablingContraints() {
        User user = (User) emProvider.em().createQuery("select u from User u where u.id = 1").getSingleResult();
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1);
    }

    @Test
    @DataSet(value = "datasets/yml/users.yml", executeStatementsBefore = "SET DATABASE REFERENTIAL INTEGRITY FALSE;")
    public void shouldSeedDataSetDisablingContraintsViaStatement() {
        User user = (User) emProvider.em().createQuery("select u from User u join fetch u.tweets join fetch u.followers join fetch u.tweets join fetch u.followers where u.id = 1").getSingleResult();
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1); 
        assertThat(user.getTweets()).hasSize(1);
    }


    @Test
    @DataSet(value = "datasets/yml/users.yml",
            useSequenceFiltering = false,
            executeStatementsBefore = "DELETE FROM User"//needed because other tests creates users and as the dataset is not created in this test the CLEAN is not performed
    )
    public void shouldNotSeedDataSetWithoutSequenceFilter() {
        List<User> users =  emProvider.em().createQuery("select u from User u join fetch u.tweets join fetch u.followers").getResultList();
        assertThat(users).isEmpty();
    }

    @Test
    @DataSet(value = "datasets/yml/users.yml",
            useSequenceFiltering = false,
            tableOrdering = {"USER","TWEET","FOLLOWER"},
            executeStatementsBefore = {"DELETE FROM FOLLOWER","DELETE FROM TWEET","DELETE FROM USER"}//needed because other tests created user dataset
    )
    public void shouldSeedDataSetUsingTableCreationOrder() {
        List<User> users =  emProvider.em().createQuery("select u from User u left join fetch u.tweets left join fetch u.followers").getResultList();
        assertThat(users).hasSize(2);
    }


    @Test
    @DataSet(value = "datasets/yml/users.yml", useSequenceFiltering = true)
    public void shouldSeedUserDataSet() {
        User user = (User) emProvider.em().createQuery("select u from User u join fetch u.tweets join fetch u.followers where u.id = 1").getSingleResult();
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1);
        assertThat(user.getTweets()).isNotNull().hasSize(1);
        Tweet tweet = user.getTweets().get(0);
        assertThat(tweet).isNotNull();
        Calendar date = tweet.getDate();
        Calendar now = Calendar.getInstance();
        assertThat(date.get(Calendar.DAY_OF_MONTH)).isEqualTo(now.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    @DataSet(value = "datasets/yml/users.yml")
    public void shouldLoadUserFollowers() {
        User user = (User) emProvider.em().createQuery("select u from User u join fetch u.tweets join fetch u.followers left join fetch u.followers where u.id = 1").getSingleResult();
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1);
        assertThat(user.getTweets()).hasSize(1);
        assertEquals(user.getTweets().get(0).getContent(), "dbunit rules!");
        assertThat(user.getFollowers()).isNotNull().hasSize(1);
        Follower expectedFollower = new Follower(2,1);
        assertThat(user.getFollowers()).contains(expectedFollower);
    }

    @Test
    @DataSet(value = "datasets/json/users.json")
    public void shouldLoadUsersFromJsonDataset() {
        User user = (User) emProvider.em().createQuery("select u from User u join fetch u.tweets join fetch u.followers left join fetch u.followers where u.id = 1").getSingleResult();
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1);
        assertThat(user.getTweets()).hasSize(1);
        assertEquals("dbunit rules json example",user.getTweets().get(0).getContent());
        assertThat(user.getFollowers()).isNotNull().hasSize(1);
        Follower expectedFollower = new Follower(2,1);
        assertThat(user.getFollowers()).contains(expectedFollower);
    }

    @Test
    @DataSet(value = "datasets/xml/users.xml")
    public void shouldLoadUsersFromXmlDataset() {
        User user = (User) emProvider.em().createQuery("select u from User u join fetch u.tweets join fetch u.followers left join fetch u.followers where u.id = 1").getSingleResult();
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1);
        assertThat(user.getTweets()).hasSize(1);
        assertEquals("dbunit rules flat xml example",user.getTweets().get(0).getContent());
        assertThat(user.getFollowers()).isNotNull().hasSize(1);
        Follower expectedFollower = new Follower(2,1);
        assertThat(user.getFollowers()).contains(expectedFollower);
    }

    @Test
    @DataSet(strategy = SeedStrategy.INSERT, value = "yml/user.yml, yml/tweet.yml, yml/follower.yml",  executeStatementsBefore = {"DELETE FROM FOLLOWER","DELETE FROM TWEET","DELETE FROM USER"})
    public void shouldLoadDataFromMultipleDataSets(){
        User user = (User) emProvider.em().createQuery("select u from User u join fetch u.tweets join fetch u.followers left join fetch u.followers where u.id = 1").getSingleResult();
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1);
        assertThat(user.getTweets()).hasSize(1);
        assertEquals("dbunit rules again!",user.getTweets().get(0).getContent());
        assertThat(user.getFollowers()).isNotNull().hasSize(1);
        Follower expectedFollower = new Follower(2,1);
        assertThat(user.getFollowers()).contains(expectedFollower);
    }

    @AfterClass//optional
    public static void close() throws SQLException {
        DataSetExecutorImpl.getExecutorById(DataSetExecutorImpl.DEFAULT_EXECUTOR_ID).getConnection().close();
    }

}
