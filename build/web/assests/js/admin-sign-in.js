/* global Swal */

async function signIn() {
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const signIn = {
        email: email,
        password: password
    };

    const signInJson = JSON.stringify(signIn);

    const response = await fetch(
            "AdminSignIn",
            {
                method: "POST",
                body: signInJson,
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
                title: 'Signed In',
                text: 'Check Your Email to find verification code!',
                showConfirmButton: false,
                timer: 1500
            }).then(() => {

                window.location = "admin-verification.html";

            });

        } else {
            Swal.fire({
                icon: 'error',
                title: 'Sign In Failed',
                text: json.message,
                confirmButtonText: 'Try Again'
            });

        }

    } else {
        Swal.fire({
            icon: 'error',
            title: 'Server Error',
            text: 'Sign in failed. Please try again.',
            confirmButtonText: 'OK'
        });
    }


}

