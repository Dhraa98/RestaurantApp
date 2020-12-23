package com.zomato.retrofit

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class RestaurantModel {
    @SerializedName("location")
    @Expose
    var location: Location? = null

    @SerializedName("popularity")
    @Expose
    var popularity: Popularity? = null

    @SerializedName("link")
    @Expose
    var link: String? = null

    @SerializedName("nearby_restaurants")
    @Expose
    var nearbyRestaurants: List<NearbyRestaurant>? = null
    class BgColor {
        @SerializedName("type")
        @Expose
        var type: String? = null

        @SerializedName("tint")
        @Expose
        var tint: String? = null
    }
    class HasMenuStatus {
       /* @SerializedName("delivery")
        @Expose
        var delivery = false

        @SerializedName("takeaway")
        @Expose
        var takeaway = 0*/
    }
    class Location {
        @SerializedName("entity_type")
        @Expose
        var entityType: String? = null

        @SerializedName("entity_id")
        @Expose
        var entityId = 0

        @SerializedName("title")
        @Expose
        var title: String? = null

        @SerializedName("latitude")
        @Expose
        var latitude: String? = null

        @SerializedName("longitude")
        @Expose
        var longitude: String? = null

        @SerializedName("city_id")
        @Expose
        var cityId = 0

        @SerializedName("city_name")
        @Expose
        var cityName: String? = null

        @SerializedName("country_id")
        @Expose
        var countryId = 0

        @SerializedName("country_name")
        @Expose
        var countryName: String? = null
    }

    class Location_ {
        @SerializedName("address")
        @Expose
        var address: String? = null

        @SerializedName("locality")
        @Expose
        var locality: String? = null

        @SerializedName("city")
        @Expose
        var city: String? = null

        @SerializedName("city_id")
        @Expose
        var cityId = 0

        @SerializedName("latitude")
        @Expose
        var latitude: String? = null

        @SerializedName("longitude")
        @Expose
        var longitude: String? = null

        @SerializedName("zipcode")
        @Expose
        var zipcode: String? = null

        @SerializedName("country_id")
        @Expose
        var countryId = 0

        @SerializedName("locality_verbose")
        @Expose
        var localityVerbose: String? = null
    }
    class NearbyRestaurant {
        @SerializedName("restaurant")
        @Expose
        var restaurant: Restaurant_? = null
    }
    class Popularity {
        @SerializedName("popularity")
        @Expose
        var popularity: String? = null

        @SerializedName("nightlife_index")
        @Expose
        var nightlifeIndex: String? = null

        @SerializedName("nearby_res")
        @Expose
        var nearbyRes: List<String>? = null

        @SerializedName("top_cuisines")
        @Expose
        var topCuisines: List<String>? = null

        @SerializedName("popularity_res")
        @Expose
        var popularityRes: String? = null

        @SerializedName("nightlife_res")
        @Expose
        var nightlifeRes: String? = null

        @SerializedName("subzone")
        @Expose
        var subzone: String? = null

        @SerializedName("subzone_id")
        @Expose
        var subzoneId = 0

        @SerializedName("city")
        @Expose
        var city: String? = null
    }
    class R {
        @SerializedName("res_id")
        @Expose
        var resId = 0

        @SerializedName("is_grocery_store")
        @Expose
        var isGroceryStore = false

        @SerializedName("has_menu_status")
        @Expose
        var hasMenuStatus: HasMenuStatus? = null
    }
    class RatingObj {
        @SerializedName("title")
        @Expose
        var title: Title? = null

        @SerializedName("bg_color")
        @Expose
        var bgColor: BgColor? = null
    }
    class UserRating {
        @SerializedName("aggregate_rating")
        @Expose
        var aggregateRating: String? = null

        @SerializedName("rating_text")
        @Expose
        var ratingText: String? = null

        @SerializedName("rating_color")
        @Expose
        var ratingColor: String? = null

        @SerializedName("rating_obj")
        @Expose
        var ratingObj: RatingObj? = null

        @SerializedName("votes")
        @Expose
        var votes = 0
    }
    class Title {
        @SerializedName("text")
        @Expose
        var text: String? = null
    }
    class Restaurant_ {
        @SerializedName("R")
        @Expose
        var r: R? = null

        @SerializedName("apikey")
        @Expose
        var apikey: String? = null

        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("url")
        @Expose
        var url: String? = null

        @SerializedName("location")
        @Expose
        var location: Location_? = null

        @SerializedName("switch_to_order_menu")
        @Expose
        var switchToOrderMenu = 0

        @SerializedName("cuisines")
        @Expose
        var cuisines: String? = null

        @SerializedName("average_cost_for_two")
        @Expose
        var averageCostForTwo = 0

        @SerializedName("price_range")
        @Expose
        var priceRange = 0

        @SerializedName("currency")
        @Expose
        var currency: String? = null

        @SerializedName("offers")
        @Expose
        var offers: List<Any>? = null

        @SerializedName("opentable_support")
        @Expose
        var opentableSupport = 0

        @SerializedName("is_zomato_book_res")
        @Expose
        var isZomatoBookRes = 0

        @SerializedName("mezzo_provider")
        @Expose
        var mezzoProvider: String? = null

        @SerializedName("is_book_form_web_view")
        @Expose
        var isBookFormWebView = 0

        @SerializedName("book_form_web_view_url")
        @Expose
        var bookFormWebViewUrl: String? = null

        @SerializedName("book_again_url")
        @Expose
        var bookAgainUrl: String? = null

        @SerializedName("thumb")
        @Expose
        var thumb: String? = null

        @SerializedName("user_rating")
        @Expose
        var userRating: UserRating? = null

        @SerializedName("photos_url")
        @Expose
        var photosUrl: String? = null

        @SerializedName("menu_url")
        @Expose
        var menuUrl: String? = null

        @SerializedName("featured_image")
        @Expose
        var featuredImage: String? = null

        @SerializedName("medio_provider")
        @Expose
        var medioProvider = false

        @SerializedName("has_online_delivery")
        @Expose
        var hasOnlineDelivery = 0

        @SerializedName("is_delivering_now")
        @Expose
        var isDeliveringNow = 0

        @SerializedName("store_type")
        @Expose
        var storeType: String? = null

        @SerializedName("include_bogo_offers")
        @Expose
        var includeBogoOffers = false

        @SerializedName("deeplink")
        @Expose
        var deeplink: String? = null

        @SerializedName("order_url")
        @Expose
        var orderUrl: String? = null

        @SerializedName("order_deeplink")
        @Expose
        var orderDeeplink: String? = null

        @SerializedName("is_table_reservation_supported")
        @Expose
        var isTableReservationSupported = 0

        @SerializedName("has_table_booking")
        @Expose
        var hasTableBooking = 0

        @SerializedName("events_url")
        @Expose
        var eventsUrl: String? = null
    }
}