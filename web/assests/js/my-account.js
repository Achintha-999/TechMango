/* global Swal */

function loadData() {
    getUserData();
    getCityData();
    getOrderData();
}

async function getUserData() {
    const response = await fetch("MyAccount");
    if (response.ok) {
        const json = await response.json();
        document.getElementById("username").innerHTML = `Hello, ${json.firstName} ${json.lastName}!`;
        document.getElementById("since").innerHTML = ` TechMango Member Since ${json.since}`;
        document.getElementById("firstName").value = json.firstName;
        document.getElementById("lastName").value = json.lastName;
        document.getElementById("mobile").value = json.mobile;
        document.getElementById("currentPassword").value = json.password;

        if (json.hasOwnProperty("addressList") && json.addressList !== undefined) {
            let email;
            let lineOne;
            let lineTwo;
            let city;
            let postalCode;
            let cityId;
            let mobile;
            const addressUL = document.getElementById("addressUL");
            json.addressList.forEach(address => {
                email = address.user.email;
                lineOne = address.lineOne;
                lineTwo = address.lineTwo;
                city = address.city.name;
                postalCode = address.postalCode;
                cityId = address.city.id;
                mobile = address.user.mobile;

            });
            document.getElementById("addName").innerHTML = `Name: ${json.firstName} ${json.lastName}`;
            document.getElementById("addEmail").innerHTML = `Email: ${email}`;
            document.getElementById("contact").innerHTML = `Phone: ${mobile}`;
            document.getElementById("address").innerHTML = `Address : ${lineOne}  ,<br/>
                    ${lineTwo}  ,<br/> 
                    ${city} <br/>`;
            document.getElementById("postalcode").innerHTML = `Postal Code : ${postalCode} `;
            document.getElementById("lineOne").value = lineOne;
            document.getElementById("lineTwo").value = lineTwo;
            document.getElementById("postalCode").value = postalCode;
            document.getElementById("citySelect").value = parseInt(cityId);
        }
    }

}

async function getCityData() {
    const response = await fetch("CityData");
    if (response.ok) {
        const json = await response.json();
        const citySelect = document.getElementById("citySelect");
        json.forEach(city => {
            let option = document.createElement("option");
            option.innerHTML = city.name;
            option.value = city.id;
            citySelect.appendChild(option);
        });

    }
}

async function saveChanges() {


    const firstName = document.getElementById("firstName").value;
    const lastName = document.getElementById("lastName").value;
    const lineOne = document.getElementById("lineOne").value;
    const lineTwo = document.getElementById("lineTwo").value;
    const postalCode = document.getElementById("postalCode").value;
    const cityId = document.getElementById("citySelect").value;
    const mobile = document.getElementById("mobile").value;
    const currentPassword = document.getElementById("currentPassword").value;
    const newPassword = document.getElementById("newPassword").value;
    const confirmPassword = document.getElementById("confirmPassword").value;

    const userDataObject = {
        firstName: firstName,
        lastName: lastName,
        lineOne: lineOne,
        lineTwo: lineTwo,
        postalCode: postalCode,
        cityId: cityId,
        mobile: mobile,
        currentPassword: currentPassword,
        newPassword: newPassword,
        confirmPassword: confirmPassword
    };

    const userDataJSON = JSON.stringify(userDataObject);

    const response = await fetch("MyAccount", {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: userDataJSON
    });
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            getUserData();
            Swal.fire({
                icon: 'success',
                title: 'Details Updated',
                text: json.message

            });
        } else {
            Swal.fire({
                icon: 'error',
                title: 'Oops...',
                text: json.message

            });

        }
    } else {
        Swal.fire({
            icon: 'error',
            title: 'Oops...',
            text: "Profile details update failed!"

        });

    }
}

async function getOrderData() {
    const response = await fetch("Order");
    if (response.ok) {
        const json = await response.json();
     //   console.log(json);

        const ordersListDiv = document.querySelector(".orders-list");
        ordersListDiv.innerHTML = ""; 

        if (json.status && json.orders) {
            for (let orderId in json.orders) {
                const items = json.orders[orderId];

               
                const firstItem = items[0];
                const orderDate = firstItem.date;
                const orderStatus = firstItem.status;

         
                let orderCard = `
                <div class="order-card">
                    <div class="order-header">
                        <div class="order-info">
                            <h4>Order <span>#${orderId}</span></h4>
                            <p class="order-date">Ordered on <span>${orderDate}</span></p>
                        </div>
                        <div class="order-status">
                            <span class="badge bg-success">${orderStatus}</span>
                        </div>
                    </div>

                    <div class="order-items">
                `;

                let total = 0;

                for (let item of items) {
                    const itemTitle = item.product_title;
                    const itemQty = item.qty;
                    const itemPrice = item.price;
                    const itemTotal = item.total;
                    total += itemTotal;

                    orderCard += `
                    <div class="order-item">
                        <div class="item-image">
                            <img src="product-images\\${item.product_id}\\image1.png" alt="${itemTitle}">
                        </div>
                        <div class="item-details">
                            <h5>${itemTitle}</h5>
                            <p class="item-price">Rs. ${itemPrice.toLocaleString()} x ${itemQty}</p>
                        </div>
                    </div>
                    `;
                }

                orderCard += `
                    </div> <!-- end order-items -->

                    <div class="order-footer">
                        <div class="order-total">
                            <p>Total: <strong>Rs. ${total.toLocaleString()}</strong></p>
                        </div>
                        <div class="order-actions">
                            <a href="#" class="btn btn-outline-primary btn-sm">View Details</a>
                            <a href="#" class="btn btn-outline-secondary btn-sm">Reorder</a>
                        </div>
                    </div>
                </div>
                `;

            
                ordersListDiv.innerHTML += orderCard;
            }
        } else {
            ordersListDiv.innerHTML = `<p class="text-muted">No orders found.</p>`;
        }

    }

}

