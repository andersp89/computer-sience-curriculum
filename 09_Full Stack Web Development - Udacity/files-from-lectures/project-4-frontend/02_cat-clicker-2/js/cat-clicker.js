var timesClicked = 0;

function catClicker() {
	var catList = $('#cat-list')
	var catName = $('#cat-name');
	var catPicture = $('#cat-picture');
	var catNumber = $('#times-clicked');
	
	// Reset DOM elements
	catNumber.text("");
	catName.text("");
	catPicture.text("");
	catList.text("");

	var nameList = ["Gubbi", "Pedro", "Peter Mathiesen", "Anders"]
	var imgList = ["cat1.jpg", "cat2.jpg", "cat3.jpg", "cat4.jpg"]

	// Make list
	for (i=0; i<imgList.length; i++) {
		var img = 'img/' + imgList[i];

		var elem = document.createElement('img');
		elem.id = "little-cat"
		elem.src = img;

		elem.addEventListener('click', (function(imgCopy) {
			return function() {
				catName.text("");
				catPicture.text("");
				catNumber.text("");
				catName.append(nameList[imgCopy]);
				catPicture.append('<img src="img/' + imgList[imgCopy] + '">');
				// Times clicked
				timesClicked = timesClicked + 1
				catNumber.append(timesClicked)
			};
		})(i));

		catList.append(elem);
	}

};
window.onload = catClicker;