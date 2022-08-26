package me.rgunny.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import me.rgunny.querydsl.entity.Member;
import me.rgunny.querydsl.entity.QMember;
import me.rgunny.querydsl.entity.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static me.rgunny.querydsl.entity.QMember.member;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @PersistenceContext
    EntityManager em;

    /**
     * JPAQueryFactory 필드 제공시 동시성 문제
     * - 동시성 문제는 JPAQueryFactory 를 생성할 때 제공하는 EntityManager(em)에 달려있다.
     * - 스프링 프레임워크는 여러 쓰레드에서 동시에 같은 EntityManager 에 접근해도, 트랜잭션 마다 별도의 영속성 컨텍스트를 제공하기 때문에, 동시성 문제는 걱정하지 않아도 된다.
     */
    JPAQueryFactory queryFactory;


    @BeforeEach
    void before() {
        queryFactory = new JPAQueryFactory(em);

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    void startJPQL() {
        // find member1
        String qlString =
                "select m from Member m where m.username = :username";

        Member findMember = em.createQuery(qlString, Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    void startQuerydsl() {
        QMember m = new QMember("m");

        Member findMember = queryFactory
                .selectFrom(m)
                .from(m)
                .where(m.username.eq("member1")) // 파라미터 바인딩 처리
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    void querydslUsingQType() {
        Member findMember = queryFactory
                .selectFrom(member)
                .from(member)
                .where(member.username.eq("member1")) // 파라미터 바인딩 처리
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    void search() {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1")
                        .and(member.age.eq(10)))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    /**
     *  where() 에 파라미터로 검색조건을 추가하면 AND 조건이 추가됨
     */
    @Test
    void searchAndParam() {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(
                        member.username.eq("member1"),
                        member.age.eq(10)
                )
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    void resultFetch() {
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch(); // 리스트 조회, 데이터 없으면 빈 리스트 반환

        Member fetchOne = queryFactory
                .selectFrom(QMember.member)
                .fetchOne(); // 단 건 조회, 데이터 없으면 `null` 반환, 둘 이상이면 `com.querydsl.NonUniqueResultException`

        Member fetchFirst = queryFactory
                .selectFrom(QMember.member)
                .fetchFirst(); // .limit(1).fetchOne()

        QueryResults<Member> fetchResults = queryFactory
                .selectFrom(member)
                .fetchResults(); // 페이징 정보 포함, total count 쿼리 추가 실행
        fetchResults.getTotal();
        fetchResults.getResults();

        long total = queryFactory
                .selectFrom(member)
                .fetchCount(); // count 쿼리로 변경해서 count 수 조회
    }


}
