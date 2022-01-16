package com.kitchenforce.domain.menus

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.kitchenforce.domain.products.entities.Product
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import javax.persistence.EntityManagerFactory

@DataJpaTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MenuProductTest @Autowired constructor(
    val emf: EntityManagerFactory
) {

    var objectmapper = ObjectMapper()

    @BeforeAll
    fun setUpData() {
        val em = emf.createEntityManager()

        try {
            em.transaction.begin()
            val menuGroup = MenuGroup(null, "샘플 매뉴 그룹")
            em.persist(menuGroup)
            val menu = Menu(null, "샘플 매뉴", 100, false, menuGroup)
            em.persist(menu)
            val product = Product(null, "샘플 상품", 100)
            em.persist(product)
            val menuProduct = MenuProduct(null, 100, menu, product)
            em.persist(menuProduct)
            em.transaction.commit()
        } catch (e: Exception) {
            em.transaction.rollback()
            throw e
        } finally {
            em.clear()
        }
    }

    @Test
    @DisplayName("MenuProduct의 단방향 관계 테스트")
    fun menuProductRelationTest() {

        // TODO JSR-310 이슈로 LocalDateTime 직렬화 에러 방지를 위한 설정코드
        // 나중에 Configuration 설정 보완 예정입니다.
        objectmapper.registerModule(JavaTimeModule())
        objectmapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        val em = emf.createEntityManager()

        /*
         * 쿼리 결과가 즉시 로딩(eager)
         *     select
                    menuproduc0_.id as id1_2_0_,
                    menuproduc0_.created_at as created_2_2_0_,
                    menuproduc0_.menu_id as menu_id4_2_0_,
                    menuproduc0_.product_id as product_5_2_0_,
                    menuproduc0_.quantity as quantity3_2_0_,
                    menu1_.id as id1_0_1_,
                    menu1_.created_at as created_2_0_1_,
                    menu1_.is_hidden as is_hidde3_0_1_,
                    menu1_.menu_group_id as menu_gro6_0_1_,
                    menu1_.name as name4_0_1_,
                    menu1_.price as price5_0_1_,
                    menugroup2_.id as id1_1_2_,
                    menugroup2_.created_at as created_2_1_2_,
                    menugroup2_.name as name3_1_2_,
                    product3_.id as id1_3_3_,
                    product3_.created_at as created_2_3_3_,
                    product3_.name as name3_3_3_,
                    product3_.price as price4_3_3_
                from
                    menu_product menuproduc0_
                left outer join
                    menu menu1_
                    on menuproduc0_.menu_id=menu1_.id
                left outer join
                    menu_group menugroup2_
                    on menu1_.menu_group_id=menugroup2_.id
                left outer join
                    product product3_
                    on menuproduc0_.product_id=product3_.id
                where
                    menuproduc0_.id=?
         */
        val result = em.find(MenuProduct::class.java, 1)
        println("==> ${objectmapper.writerWithDefaultPrettyPrinter().writeValueAsString(result)}")

        assertAll(
            { assertNotNull(result.product) },
            { assertNotNull(result.menu) },
            { assertNotNull(result.id) }
        )
    }

    @Test
    @DisplayName("Product 조회 테스트")
    fun productJoinTest() {
        objectmapper.registerModule(JavaTimeModule())
        objectmapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        val em = emf.createEntityManager()

        val result = em.find(Product::class.java, 1)
        println("==> ${objectmapper.writerWithDefaultPrettyPrinter().writeValueAsString(result)}")

        /*
         * Product는 아무 연관관계가 없으므로 단일 테이블 select 결과가 나와야 한다.
         *     select
                    product0_.id as id1_3_0_,
                    product0_.created_at as created_2_3_0_,
                    product0_.name as name3_3_0_,
                    product0_.price as price4_3_0_
                from
                    product product0_
                where
                    product0_.id=?
         */
        assertAll(
            { assertNotNull(result.name) },
            { assertNotNull(result.price) },
            { assertNotNull(result.id) },
            { assertNotNull(result.createdAt) }
        )

        val expect = """
            {
              "createdAt" : "${result.createdAt}",
              "id" : ${result.id},
              "name" : "${result.name}",
              "price" : ${result.price}
            }
        """.trimIndent()

        assertEquals(expect, objectmapper.writerWithDefaultPrettyPrinter().writeValueAsString(result).trimIndent())
    }
}
