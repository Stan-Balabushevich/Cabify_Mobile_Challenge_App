package id.slava.nt.cabifymobilechallengeapp.util

import id.slava.nt.cabifymobilechallengeapp.data.local.database.db_object.ProductEntity
import id.slava.nt.cabifymobilechallengeapp.data.remote.dt_object.ProductDiscount
import id.slava.nt.cabifymobilechallengeapp.data.remote.dt_object.ProductDto
import id.slava.nt.cabifymobilechallengeapp.domain.model.Product

fun product(): Product = Product(
    id = 1,
    name = "Product 1",
    code = "P1",
    price = 100.0
)

fun productEntity(): ProductEntity = ProductEntity(
    id = 1,
    code = "Product 1",
    name = "P1",
    price = 100.0,
    lastUpdated = System.currentTimeMillis())

fun productDto(): ProductDto = ProductDto(code = "1", name = "Product 1", price = 100.0)

fun productBulkDiscount(): ProductDiscount.BulkDiscount =
   ProductDiscount.BulkDiscount(3, 19.0)

fun productBuyXGetYFreeDiscount(): ProductDiscount.BuyXGetYFree =
ProductDiscount.BuyXGetYFree(2, 1)

