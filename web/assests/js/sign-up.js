/* global Swal */

async  function signUp() {
    const firstName = document.getElementById("firstName").value;
    const lastName = document.getElementById("lastName").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const mobile = document.getElementById("mobile").value;

    const user = {
        firstName: firstName,
        lastName: lastName,
        email: email,
        password: password,
        mobile:mobile
    };

    const userJson = JSON.stringify(user);

     const response = await  fetch(
            "SignUp",
            {
                method: "POST",
                body: userJson,
                header: {
                    "Content-Type": "application/json"
                }
            }
    );
        if (response.ok) {
            const json = await response.json();
            if (json.status) {
             
                Swal.fire({
                    icon: 'success',
                    title: 'Registration Successful!',
                    text: json.message,
                    confirmButtonText: 'Verify Account'
                }).then(() => {
                    window.location = "verify-account.html";
                });
            } else {
              
                Swal.fire({
                    icon: 'error',
                    title: 'Oops...',
                    text: json.message,
                    confirmButtonText: 'Try Again'
                });
            }
        } else {
         
            Swal.fire({
                icon: 'error',
                title: 'Server Error',
                text: 'Registration failed. Please try again later.',
                confirmButtonText: 'OK'
            });
        }
}

