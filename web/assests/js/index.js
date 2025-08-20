/* global Swal */

function indexOnloadFunctions() {
    checkSessionCart();
    loadProductData();
}
async function checkSessionCart() {
    const response = await fetch("CheckSessionCart");
    if (!response.ok) {
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: "Something went wrong. Try again"

        });
    }
}

async function loadProductData() {
    const response = await fetch("LoadHomeData");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
   
            loadNewArrivals(json);
        } else {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: "Something went wrong. Try again"

            });
        }
    } else {
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: "Something went wrong. Try again"

        });
    }
}


function loadNewArrivals(json) {
    const featured_product_container = document.getElementById("featured-product-container");
    featured_product_container.innerHTML = "";

    json.productList.forEach(item => {
        let product_card = `   <div class="col-lg-3 col-md-4 col-6">
                            <div class="product-card">
                                <div class="product-thumb">
        
                                    <a href="single-product.html?id=${item.id}"><img src="product-images\\${item.id}\\image1.png" alt="Product Images"></a>
                                </div>
                                <div class="product-details">
                                    <h4><a href="single-product.html?id=${item.id}">${item.title}</a></h4>
                                    <div class="product-price">
                                        <span class="price">Rs. ${new Intl.NumberFormat(
                "en-US",
                {minimumFractionDigits: 2})
                .format(item.price)}</span>        
                                    </div>
                                    <div class="product-bottom">
                                        <a class="btn btn-sm btn-outline-secondary" onclick="addToCart(${item.id},1);">Add to Cart</a>
                                        <a href="#" class="wishlist-btn"><i class="far fa-heart"></i></a>
                                    </div>
                                </div>
                            </div>
                        </div>`;
        featured_product_container.innerHTML += product_card;
    });
}

async function addToCart(productId, qty) {
    const response = await fetch("AddToCart?prId=" + productId + "&qty=" + qty);
    if (response.ok) {
        const json = await response.json(); // await response.text();
        if (json.status) {

            Swal.fire({
                icon: 'success',
                title: 'Success',
                text: json.message

            });

        } else {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: "Something went wrong. Try again"

            });


        }
    } else {
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: "Something went wrong. Try again"

        });

    }
}
