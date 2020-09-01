var timesClicked = 0;

function catClicker() {
	var catName1 = $('#cat-name1');
	var catPicture1 = $('#cat-picture1');
	var catName2 = $('#cat-name2');
	var catPicture2 = $('#cat-picture2');
	var catNumber = $('#times-clicked');
	
	// Reset DOM elements
	catNumber.text("");
	catName1.text("");
	catPicture1.text("");
	catName2.text("");
	catPicture2.text("");

	// Times clicked
	timesClicked = timesClicked + 1
	catNumber.append(timesClicked)


	var nameList = ["none", "Gubbi", "Pedro", "Peter Mathiesen", "Anders"]
	// Cat selector 1
	randomSelection1 = Math.floor((Math.random() * 4) + 1);
	catName1.append(nameList[randomSelection1]);
	selectedPicture1 = "img/cat" + randomSelection1 + ".jpg";
	catPicture1.append('<img src="' + selectedPicture1 + '">' );

	// Cat selector 2
	randomSelection2 = Math.floor((Math.random() * 4) + 1);
	catName2.append(nameList[randomSelection2]);
	selectedPicture2 = "img/cat" + randomSelection2 + ".jpg";
	catPicture2.append('<img src="' + selectedPicture2 + '">' );
};

$('#cat-button').click(catClicker);

// Dual cats