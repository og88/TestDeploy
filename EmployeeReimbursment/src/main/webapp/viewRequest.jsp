<!DOCTYPE html>
<html>

<head>
	<meta charset="ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1" />

	<link href="https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-GJzZqFGwb1QTTN6wy59ffF1BuGJpLSa9DkKMp0DgiMDm4iYMj70gZWKYbI706tWS"
	 crossorigin="anonymous">
	<title>View all Request</title>
</head>

<body class="container">
	<div>
		<h1>Requests</h1>
		<button id="GoBack">Go Back</button>
		</h1>
	</div>
	<div>
		<table class="table table-bordered table-striped ">
			<tbody id="myTable">
				<th>ID</th>
				<th>Type</th>
				<th>Amount</th>
				<th>Description</th>
				<th>User</th>
				<th>Manager</th>
				<th>Status</th>
			</tbody>
		</table>
	</div>
	<script>
		window.onload = function () {
			console.log(
				<%=session.getAttribute("rType")%>
			);
			document.getElementById("GoBack").addEventListener('click',
				function () {
					window.history.back();
				})
			let xhr = new XMLHttpRequest();
			console.log(
				<%=session.getAttribute("manager")%>
			);
			xhr.onreadystatechange = function () {
				if (this.readyState == 4 && this.status == 200) {
					console.log(xhr.responseText);
					var jsonData = JSON.parse(xhr.responseText);
					for (i = 0; i < jsonData.length; i++) {
						if (
							<%=session.getAttribute("manager")%> ||
							(!
								<%=session.getAttribute("manager")%> &&
								<%=session.getAttribute("id")%> ==
								jsonData[i].userId)) {
							let row = document.createElement("tr");
							let idcol = document.createElement("td");
							let tcol = document.createElement("td");
							let amcol = document.createElement("td");
							let dcol = document.createElement("td");
							let ucol = document.createElement("td");
							let mcol = document.createElement("td");
							let apcol = document.createElement("td");

							idcol.textContent = jsonData[i].id;
							tcol.textContent = jsonData[i].type;
							amcol.textContent = jsonData[i].amount;
							dcol.textContent = jsonData[i].description;
							ucol.textContent = jsonData[i].userId;


							row.appendChild(idcol);
							row.appendChild(tcol);
							row.appendChild(amcol);
							row.appendChild(dcol);
							row.appendChild(ucol);
							if (jsonData[i].approved == 0) {
								let resolve = document.createElement("div");
								resolve.innerHTML =
									'<form><button formaction="HTML/loginPage.html" onclick="resol()">Resolve</button></form>';
								resol = function () {
									console.log("function inside button");
								}
								mcol.textContent = "N/A";
								row.appendChild(mcol);
								row.appendChild(resolve);

							} else {
								apcol.textContent = jsonData[i].approved;
								mcol.textContent = jsonData[i].manager;
								row.appendChild(mcol);
								row.appendChild(apcol);
							}
							document.getElementById("myTable").appendChild(row);
						}
						console.log("work");
					}
				}
			}

			xhr
				.open('GET',
					'http://localhost:8080/EmployeeReimbursment/rest/viewAllRequests');
			xhr.send();
		}
	</script>
</body>

</html>