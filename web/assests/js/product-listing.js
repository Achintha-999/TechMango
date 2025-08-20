/* global Swal */
let modelList;
async function loadProductData() {
    const response = await fetch("LoadProductData");
    if (response.ok) {
        const json = await response.json();
        //   console.log(json);
        if (json.status) {
            loadSelect("brand", json.brandList, "name");
            loadSelect("brand-a", json.brandList, "name");
            modelList = json.modelList;
//            loadSelect("model", json.modelList, "name");
            loadSelect("storage-selector", json.storageList, "name");
            loadSelect("category-selector", json.categoryList, "name");
            loadSelect("color", json.colorList, "name");
            loadSelect("condition", json.qualityList, "name");
            loadSelect("ram-selector", json.ramList, "name");
            loadSelect("processor-selector", json.processorList, "name");
            loadSelect("graphic", json.graphicList, "name");
        } else {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: "Something went wrong. Please try again later",
                confirmButtonText: 'Try Again'
            });

        }
    } else {
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: "Product loading failed. Please try again",
            confirmButtonText: 'Try Again'
        });

    }
}

function loadSelect(selectId, items, property) {
    const select = document.getElementById(selectId);
    items.forEach(item => {
        const option = document.createElement("option");
        option.value = item.id;
        option.innerHTML = item[property];
        select.appendChild(option);
    });
}

function loadModels() {
    const brandId = document.getElementById("brand").value;
    const modelSelect = document.getElementById("model");
    modelSelect.length = 1;
    modelList.forEach(item => {
        if (item.brand.id === parseInt(brandId)) {
            const option = document.createElement("option");
            option.value = item.id;
            option.innerHTML = item.name;
            modelSelect.appendChild(option);
        }
    });
}


function loadVariant() {
    const categorySelector = document.getElementById("category-selector");
    const categoryText = categorySelector.options[categorySelector.selectedIndex].text.toLowerCase();

    const colorSelect = document.getElementById("color");
    const storageSelect = document.getElementById("storage-selector");
    const ramSelect = document.getElementById("ram-selector");
    const processorSelect = document.getElementById("processor-selector");
    const graphicSelect = document.getElementById("graphic");

    const variantSelectors = [colorSelect, storageSelect, ramSelect, processorSelect, graphicSelect];

    const isComputer = (categoryText === "laptop" || categoryText === "desktop");

    if (!isComputer) {

        variantSelectors.forEach(select => {
            select.selectedIndex = 0;
            select.disabled = true;
        });
    } else {

        variantSelectors.forEach(select => {
            select.disabled = false;
        });
    }
}

async function saveProduct() {
    const brandId = document.getElementById("brand").value;
    const modelId = document.getElementById("model").value;
    const title = document.getElementById("title").value;
    const categoryId = document.getElementById("category-selector").value;
    const description = document.getElementById("description").value;
    const storageId = document.getElementById("storage-selector").value;
    const colorId = document.getElementById("color").value;
    const conditionId = document.getElementById("condition").value;
    const ramId = document.getElementById("ram-selector").value;
    const price = document.getElementById("price").value;
    const qty = document.getElementById("qty").value;
    const  processorID = document.getElementById("processor-selector").value;
    const  graphicID = document.getElementById("graphic").value;

    const image1 = document.getElementById("img1").files[0];
    const image2 = document.getElementById("img2").files[0];
    const image3 = document.getElementById("img3").files[0];

    const form = new FormData();
    form.append("brandId", brandId);
    form.append("modelId", modelId);
    form.append("categoryId", categoryId);
    form.append("title", title);
    form.append("description", description);
    form.append("storageId", storageId);
    form.append("colorId", colorId);
    form.append("conditionId", conditionId);
    form.append("ramId", ramId);
    form.append("processorID", processorID);
    form.append("graphicID", graphicID);
    form.append("price", price);
    form.append("qty", qty);
    form.append("image1", image1);
    form.append("image2", image2);
    form.append("image3", image3);

    const response = await fetch("SaveProduct", {
        method: "POST",
        body: form
    });
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            Swal.fire({
                icon: 'success',
                title: 'Product has been saved successfully!',
                text: "New product added successfully",
                showConfirmButton: false,
                timer: 1500
            });

            document.getElementById("brand").value = 0;
            document.getElementById("model").value = 0;
            document.getElementById("category-selector").value = 0;
            document.getElementById("title").value = "";
            document.getElementById("description").value = "";
            document.getElementById("storage-selector").value = 0;
            document.getElementById("processor-selector").value = 0;
            document.getElementById("graphic").value = 0;
            document.getElementById("ram-selector").value = 0;
            document.getElementById("color").value = 0;
            document.getElementById("condition").value = 0;
            document.getElementById("price").value = "0.00";
            document.getElementById("qty").value = 1;
            document.getElementById("img1").value = "";
            document.getElementById("img2").value = "";
            document.getElementById("img3").value = "";
        } else {
            if (json.message === "Please sign in!") {
                window.location = "admin-sign-in.html";
            } else {

                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: json.message,
                    confirmButtonText: 'Try Again'
                });

            }
        }
    } else {

    }
}

async function   loadUsers() {
    const response = await fetch("Users");
    if (response.ok) {
        const json = await response.json();
        //    console.log(json);
        const users_container = document.getElementById("users-container");
        users_container.innerHTML = "";



        const totalUsers = json.users.length;
        document.getElementById("user-count").innerText = totalUsers;

        json.users.forEach(user => {


            let tableData = ` <tr>
                                                <td>${user.id}</td>
                                                <td>${user.first_name} ${user.last_name}</td>
                                                <td>${user.email}</td>
                                                <td>${user.mobile}</td>
                                                <td>${user.created_at}</td>
                                              
                                                <td><span class="badge bg-success">Active</span></td>
                                                <td>
                                                    <button class="btn btn-sm btn-outline-primary"><i class="fas fa-eye"></i></button>
                                                    <button class="btn btn-sm btn-outline-warning"><i class="fas fa-edit"></i></button>
                                                    <button class="btn btn-sm btn-outline-danger"><i class="fas fa-trash"></i></button>
                                                </td>
                                            </tr>
                                    `;
            users_container.innerHTML += tableData;
        });
    }

}

async function loadProducts() {
    loadBrandList();
    loadModelList();
    loadCategoryList();
    const response = await fetch("LoadAllProducts");
    if (response.ok) {
        const json = await response.json();
        //    console.log(json);  

        const productsContainer = document.getElementById("product-container");
        productsContainer.innerHTML = "";  // Clear existing content

        json.productList.forEach(item => {
            let badgeClass = item.status === "Active" ? "bg-success" : "bg-danger";
            let tableData = `
<tr id="product-${item.id}">
    <td>${item.id}</td>
    <td>
        <img src="product-images/${item.id}/image1.png" 
             class="rounded" alt="product image" 
             style="width: 40px; height: 30px">
    </td>
    <td>${item.title}</td>
    <td>${item.brand}</td>
    <td>Rs. ${new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.price)}</td>
    <td>${item.qty}</td>
    <td><span class="badge ${badgeClass}" onclick="changeStatus(${item.id});">
            ${item.status}
        </span></td>
    <td>
        <button class="btn btn-sm btn-outline-primary"><i class="fas fa-eye"></i></button>
        <button class="btn btn-sm btn-outline-warning"><i class="fas fa-edit"></i></button>
        <button class="btn btn-sm btn-outline-danger"><i class="fas fa-trash"></i></button>
    </td>
</tr>
`;

            productsContainer.innerHTML += tableData;
        });
    } else {
        console.error("Failed to load products:", response.status);
    }
}

async function loadOrders() {
    const response = await fetch("LoadAllOrders");
    if (response.ok) {
        const json = await response.json();

        const orderContainer = document.getElementById("orders-container");
        orderContainer.innerHTML = "";

        let todayCount = 0;
        let todayTotal = 0;
        let productSales = {};

        const today = new Date().toISOString().split("T")[0];

        json.orders.forEach(order => {
            const statusColors = {
                "Paid": "bg-success",
                "Processing": "bg-primary",
                "Shipped": "bg-info text-dark",
                "Delivered": "bg-secondary",
                "Pending": "bg-warning text-dark"
            };

            let statusClass = statusColors[order.status] || "bg-dark";

            // Count today's orders
            if (order.date === today) {
                todayCount++;
                todayTotal += order.price;
            }

            // Track product sales
            order.product_titles.forEach((title, index) => {
                let productId = order.product_ids?.[index]; // from backend
                if (!productSales[title]) {
                    productSales[title] = {
                        id: productId,
                        count: 0

                    };
                }
                productSales[title].count++;
            });

            // Render orders table row
            let tableData = `
                <tr>
                    <td>${order.id}</td>
                    <td>${order.first_name} ${order.last_name}</td>
                    <td>
                        ${order.product_titles.map(title => `<span class="badge bg-info text-dark me-1">${title}</span>`).join("")}
                    </td>
                    <td>${order.date}</td>
                    <td>${order.price.toLocaleString()}</td>
                    <td><span class="badge bg-success">Paid</span></td>
                    <td>
                        <span class="badge ${statusClass}" style="cursor:pointer;"
                              data-order-id="${order.id}"
                              onclick="changeOrderStatus(${order.id});">
                            ${order.status}
                        </span>
                    </td>
                    <td>
                        <button class="btn btn-sm btn-outline-primary"><i class="fas fa-eye"></i></button>
                        <button class="btn btn-sm btn-outline-warning"><i class="fas fa-edit"></i></button>
                    </td>
                </tr>
            `;
            orderContainer.innerHTML += tableData;
        });

        // Sort products by most sold
        const sortedProducts = Object.entries(productSales)
                .sort((a, b) => b[1].count - a[1].count)
                .slice(0, 5); // Top 5

        // Populate Most Sold Items card
        const mostSoldList = document.getElementById("most-sold-list");
        mostSoldList.innerHTML = "";
        sortedProducts.forEach(([title, data]) => {
            mostSoldList.innerHTML += `
                <div class="list-group-item d-flex justify-content-between align-items-center px-0">
                    <div class="d-flex align-items-center">
                       
                        <div>
                            <h6 class="mb-0">${title}</h6>
                 
                        </div>
                    </div>
                    <span class="badge bg-primary rounded-pill">${data.count}</span>
                </div>
            `;
        });

        // Update today's stats
        document.getElementById("today-order-count").innerText = todayCount;
        document.getElementById("today-order-total").innerText = "Rs. " + todayTotal.toLocaleString();
    } else {
        console.error("Failed to load orders");
    }
}



async function loadBrandList() {
    const response = await fetch("LoadProductData");
    if (response.ok) {
        const json = await response.json();
        //   console.log(json);  

        const brandContainer = document.getElementById("brand-container");
        brandContainer.innerHTML = "";  // Clear existing content

        json.brandList.forEach(brand => {
            let tableData = `
                     <tr>
                                                <td>${brand.id}</td>                   
                                                <td>${brand.name}</td>                                  
                                                <td><span class="badge bg-success">Active</span></td>
                                                <td>
                                                    <button class="btn btn-sm btn-outline-warning"><i class="fas fa-edit"></i></button>
                                                    <button class="btn btn-sm btn-outline-danger"><i class="fas fa-trash"></i></button>
                                                </td>
                                            </tr>
            `;
            brandContainer.innerHTML += tableData;
        });
    } else {
        console.error("Failed to load products:", response.status);
    }
}

async function loadModelList() {
    const response = await fetch("LoadModelData");
    if (response.ok) {
        const json = await response.json();
        //    console.log(json);

        const modelContainer = document.getElementById("model-container");
        modelContainer.innerHTML = "";  // Clear existing content

        json.models.forEach(model => {
            let tableData = `
                     <tr>
                                                <td>${model.id}</td>
                                                <td>${model.model_name}</td>
                                                <td>${model.brand_name}</td>
                                             
                                                <td>
                                                    <button class="btn btn-sm btn-outline-warning"><i class="fas fa-edit"></i></button>
                                                    <button class="btn btn-sm btn-outline-danger"><i class="fas fa-trash"></i></button>
                                                </td>
                                            </tr>
            `;
            modelContainer.innerHTML += tableData;
        });
    } else {
        console.error("Failed to load products:", response.status);
    }
}

async function loadCategoryList() {
    const response = await fetch("LoadData");
    if (response.ok) {
        const json = await response.json();
        //  console.log(json);

        const catContainer = document.getElementById("cat-container");
        catContainer.innerHTML = "";  // Clear existing content

        json.categoryList.forEach(category => {
            let tableData = `
                      <div class="list-group-item d-flex justify-content-between align-items-center">
            ${category.name}
                                            </div>
            `;
            catContainer.innerHTML += tableData;
        });
    } else {
        console.error("Failed to load products:", response.status);
    }
}

async function changeStatus(productId) {
    const response = await fetch("ChangeStatus?prId=" + productId);
    if (response.ok) {
        const json = await response.json(); // await response.text();
        if (json.status) {

            Swal.fire({
                icon: 'success',
                title: 'Success',
                text: json.message

            });


            let badge = document.querySelector(`#product-${productId} .badge`);
            if (badge) {
                let isActive = json.newStatus === "Active";
                badge.textContent = json.newStatus;
                badge.className = "badge " + (isActive ? "bg-success" : "bg-danger");
            }


        } else {
            Swal.fire({
                icon: 'error',
                title: 'Error',
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


async function changeOrderStatus(orderId) {
    const response = await fetch("ChangeOrderStatus?orderId=" + orderId);
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            Swal.fire({
                icon: 'success',
                title: 'Success',
                text: json.message
            });

            const statusColors = {
                "Paid": "bg-success",
                "Processing": "bg-primary",
                "Shipped": "bg-info text-dark",
                "Delivered": "bg-secondary",
                "Pending": "bg-warning text-dark"
            };

            const statusKey = (json.newStatus || "").trim();


            const badge = document.querySelector(
                    `#orders-container span[data-order-id="${orderId}"]`
                    );

            if (badge) {
                badge.textContent = statusKey;
                badge.className = `badge ${statusColors[statusKey] || "bg-dark"}`;
            }

        } else {
            Swal.fire({
                icon: 'error',
                title: 'Error',
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


function printInvoice() {

    document.querySelectorAll("th:last-child, td:last-child").forEach(el => {
        el.style.display = "none";
    });

    window.print();


    document.querySelectorAll("th:last-child, td:last-child").forEach(el => {
        el.style.display = "";
    });
}

