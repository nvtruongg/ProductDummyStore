package com.example.dummyjsonapp.data.mapper

import com.example.dummyjsonapp.data.local.entity.CategoryEntity
import com.example.dummyjsonapp.data.local.entity.ProductDimensions
import com.example.dummyjsonapp.data.local.entity.ProductEntity
import com.example.dummyjsonapp.data.local.entity.ProductReview
import com.example.dummyjsonapp.data.remote.dto.CategoryDto
import com.example.dummyjsonapp.data.remote.dto.ProductDimensionsDto
import com.example.dummyjsonapp.data.remote.dto.ProductDto
import com.example.dummyjsonapp.data.remote.dto.ProductReviewDto
import com.example.dummyjsonapp.domain.model.CategoryModel
import com.example.dummyjsonapp.domain.model.ProductDimensionsModel
import com.example.dummyjsonapp.domain.model.ProductModel
import com.example.dummyjsonapp.domain.model.ProductReviewModel

//mapper remote to local
fun ProductDto.toEntity() : ProductEntity{
    return ProductEntity(
        id = id,
        title = title,
        description = description,
        price = price,
        rating = rating,
        thumbnail = thumbnail,
        discountPercentage = discountPercentage,
        category = category,
        stock = stock,
        images = images,
        brand = brand,
        weight = weight,
        dimensions = dimensions?.toEntity(),
        warrantyInformation = warrantyInformation,
        shippingInformation = shippingInformation,
        returnPolicy = returnPolicy,
        reviews = reviews?.map { it.toEntity() }
    )
}
fun ProductDimensionsDto.toEntity(): ProductDimensions {
    return ProductDimensions(
        width = width,
        height = height,
        depth = depth
    )
}

fun ProductReviewDto.toEntity(): ProductReview {
    return ProductReview(
        rating = rating,
        comment = comment,
        date = date,
        reviewerName = reviewerName,
        reviewerEmail = reviewerEmail
    )
}

//mapper local to domain
fun ProductEntity.toDomain() : ProductModel{
    return ProductModel(
        id = id,
        title = title,
        description = description,
        price = price,
        rating = rating,
        thumbnail = thumbnail,
        discountPercentage = discountPercentage,
        category = category,
        stock = stock,
        images = images,
        brand = brand,
        weight = weight,
        dimensions = dimensions?.toDomain(),
        warrantyInformation = warrantyInformation,
        shippingInformation = shippingInformation,
        returnPolicy = returnPolicy,
        reviews = reviews?.map { it.toDomain() }
    )
}

fun ProductDimensions.toDomain(): ProductDimensionsModel {
    return ProductDimensionsModel(
        width = width,
        height = height,
        depth = depth
    )
}

fun ProductReview.toDomain(): ProductReviewModel {
    return ProductReviewModel(
        rating = rating,
        comment = comment,
        date = date,
        reviewerName = reviewerName,
        reviewerEmail = reviewerEmail
    )
}

fun CategoryDto.toEntity(): CategoryEntity {
    return CategoryEntity(
        slug = slug,
        name = name,
        url = url
    )
}

fun CategoryEntity.toDomain(): CategoryModel {
    return CategoryModel(
        slug = slug,
        name = name,
        url = url
    )
}