/* global Swal */
async function loadData() {
    const searchParams = new URLSearchParams(window.location.search);
    if (searchParams.has("id")) {
        const productId = searchParams.get("id");
        console.log(productId);
        const response = await fetch("LoadSingleProduct?id=" + productId);
        if (response.ok) {
            const json = await response.json();
            if (json.status) {
                console.log(json);
                //single-product-images
                const basePath = "product-images\\" + json.product.id + "\\";
                const mainImage = document.getElementById("mainImage");
                const thumbs = [
                    document.getElementById("thumb1"),
                    document.getElementById("thumb2"),
                    document.getElementById("thumb3")
                ];

                // Assign image sources
                mainImage.src = basePath + "image1.png";
                thumbs[0].src = basePath + "image1.png";
                thumbs[1].src = basePath + "image2.png";
                thumbs[2].src = basePath + "image3.png";
//                //single-product-images-end
                document.getElementById("cat-path").innerHTML = json.product.category.name;
                document.getElementById("product").innerHTML = json.product.title;

                document.getElementById("product-title").innerHTML = json.product.title;

                document.getElementById("product-price").innerHTML = new Intl.NumberFormat(
                        "en-US",
                        {minimumFractionDigits: 2})
                        .format(json.product.price);
                document.getElementById("brand-name").innerHTML = json.product.model.brand.name;
                document.getElementById("model-name").innerHTML = json.product.model.name;
                document.getElementById("product-quality").innerHTML = json.product.quality.name;
                document.getElementById("product-stock").innerHTML = json.product.qty;

//                //product-description
            document.getElementById("product-description").innerHTML = json.product.description;
//
//                //add-to-cart-main-button
                const addToCartMain = document.getElementById("add-to-cart-main");
                addToCartMain.addEventListener(
                        "click", (e) => {
                    addToCart(json.product.id, document.getElementById("add-to-cart-qty").value);
                    e.preventDefault();
                });
                //add-to-cart-main-button-end
                
const specsContainer = document.querySelector(".specs-table");
    specsContainer.innerHTML = ""; 

    if (json.variantList && json.variantList.length > 0) {
        const variant = json.variantList[0]; 

        const specs = [
            { name: "Color", value: variant.color.name },
            { name: "Processor", value: variant.processor.name },
            { name: "Memory", value: variant.ram.name },
            { name: "Storage", value: variant.storage.name },
            { name: "Graphics", value: variant.graphics.name }
        ];


        specs.forEach(spec => {
            const row = document.createElement("div");
            row.className = "spec-row";
            row.innerHTML = `
                <div class="spec-name">${spec.name}</div>
                <div class="spec-value">${spec.value}</div>
            `;
            specsContainer.appendChild(row);
        });

    } else {
    
    
        specsContainer.innerHTML = `
            <div class="spec-row">
                <div class="spec-name">Description</div>
                <div class="spec-value">${json.product.description}</div>
            </div>
        `;
    }
                //similer-products
                let similer_product_main = document.getElementById("smiler-product-main");
                let productHtml = document.getElementById("similer-product");
                similer_product_main.innerHTML = "";
                json.productList.forEach(item => {
                    let productCloneHtml = productHtml.cloneNode(true);
                    productCloneHtml.querySelector("#similer-product-a1").href = "single-product.html?id=" + item.id;
                    productCloneHtml.querySelector("#similer-product-image").src = "product-images\\" + item.id + "\\image1.png";
                    productCloneHtml.querySelector("#simler-product-add-to-cart").addEventListener(
                            "click", (e) => {
                        addToCart(item.id, 1);
                        e.preventDefault();
                    });
                    productCloneHtml.querySelector("#similer-product-a2").href = "single-product.html?id=" + item.id;
                    productCloneHtml.querySelector("#similer-product-title").innerHTML = item.title;
                //    productCloneHtml.querySelector("#similer-product-storage").innerHTML = item.storage.value;
                    productCloneHtml.querySelector("#similer-product-price").innerHTML = "Rs. " + new Intl.NumberFormat(
                            "en-US",
                            {minimumFractionDigits: 2})
                            .format(item.price);
                    ;
                //    productCloneHtml.querySelector("#similer-product-color-border").style.borderColor = "black";
               //     productCloneHtml.querySelector("#similer-product-color-background").style.backgroundColor = item.color.value;

                    // append the clone code
                    similer_product_main.appendChild(productCloneHtml);

                       });

            } else {
                window.location = "index.html";
            }
        } else {
            window.location = "index.html";
        }
    }
}

async function addToCart(productId, qty) {
    const response = await fetch("AddToCart?prId=" + productId + "&qty=" + qty);
    if (response.ok) {
        const json = await response.json(); // await response.text();
        if (json.status) {
            Swal.fire({
                icon: 'success',
                title: 'Added to Cart',
                text: json.message,
                showConfirmButton: false

            });

        } else {
            Swal.fire({
                icon: 'warning',
                title: 'Warning',
                text: json.message

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
