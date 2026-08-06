const mapElement = document.querySelector('gmp-map');
const typeSelect = document.querySelector('.type-select');
var innerMap, currentLocationDot, infoWindow;

const marker = document.getElementById("marker");
const latInput = document.getElementById("lat-input");
const lngInput = document.getElementById("lng-input");
const showSavedBtn = document.getElementById("show-saved-btn");
const savedLocationsPanel = document.getElementById("saved-locations-panel");

let directionsRenderer = null;
let currentRoute=null
let mapReady = false;

function createCurrentDot(){
  const div = document.createElement('div');
  div.style.width = "16px";
  div.style.height = "16px";
  div.style.backgroundColor = "blue";
  div.style.borderRadius = "50%";
  return div;
}

async function createMap() {
    const { Map, InfoWindow } = await google.maps.importLibrary('maps');
    const { AdvancedMarkerElement } = await google.maps.importLibrary('marker');
    const { DirectionsService, DirectionsRenderer } = await google.maps.importLibrary('routes');

    await mapElement.innerMapReady;
    mapReady = true;
    infoWindow = new InfoWindow();
    innerMap = mapElement.innerMap;
    mapReady = true;

    innerMap.setOptions({
        mapTypeControl: false,
    });

    navigator.geolocation.getCurrentPosition((position) => {
      const pos = {
        lat: position.coords.latitude,
        lng: position.coords.longitude
      };
      innerMap.setCenter(pos);
      currentLocationDot = new AdvancedMarkerElement({
        map: innerMap,
        position: pos,
        content: createCurrentDot(),
      });
      searchSimilar();
    })
    typeSelect.addEventListener('change', searchSimilar);
}

async function searchSimilar(){
  const [
      { Place, SearchNearbyRankPreference },
      { AdvancedMarkerElement },
      { spherical },
  ] = await Promise.all([
      google.maps.importLibrary('places'),
      google.maps.importLibrary('marker'),
      google.maps.importLibrary('geometry'),
  ]);

  const markers = mapElement.querySelectorAll('gmp-advanced-marker');
  markers.forEach(m => {
    if (m != currentLocationDot && m != marker){
      m.map = null;
    }
  })

  if (!typeSelect.value){
    return;
  }

  const center = mapElement.center;
  const ne = innerMap.getBounds().getNorthEast();
  const sw = innerMap.getBounds().getSouthWest();
  const radius = Math.min(spherical.computeDistanceBetween(ne, sw) / 2, 50000);

  const request = {
      fields: [
          'displayName',
          'location',
          'formattedAddress',
          'googleMapsURI',
      ],
      locationRestriction: {
          center,
          radius,
      },
      includedPrimaryTypes: [typeSelect.value],
      maxResultCount: 20,
      rankPreference: SearchNearbyRankPreference.DISTANCE,
  };  

  const { places } = await Place.searchNearby(request);

  if (places.length > 0){
    places.forEach((place, i) => {
      const marker = new AdvancedMarkerElement({
        map: innerMap,
        position: place.location,
      });

      const safeName = place.displayName.replace(/'/g, '');
      const lat = place.location.lat();
      const lng = place.location.lng();
      const info = `
        <div>
          <strong>${place.displayName}</strong>
          <div>${place.formattedAddress}</div>
          <button onclick="savePlace('${safeName}', ${lat}, ${lng})" style="margin-top: 10px;">Save This Location</button>
        </div>
      `;
      marker.addListener("click", (e) => {
        if (e.stop){
          e.stop();
        }
        infoWindow.setContent(info);
        infoWindow.open({
          anchor: marker,
          map: innerMap,
        })
      })
    });
  } 
  else{
    alert("No results found nearby");
  }
}

/*async function drawRoute() {

    if (!locations || locations.length < 2) {
        console.log("Not enough locations to create route");
        return;
    }


    const directionsService =
        new google.maps.DirectionsService();


    const directionsRenderer =
        new google.maps.DirectionsRenderer({
            suppressMarkers: false
        });


    directionsRenderer.setMap(innerMap);



    const origin = {
        lat: locations[0].latitude,
        lng: locations[0].longitude
    };


    const destination = {
        lat: locations[locations.length - 1].latitude,
        lng: locations[locations.length - 1].longitude
    };



    const waypoints = locations
        .slice(1, -1)
        .map(location => ({
            location: {
                lat: location.latitude,
                lng: location.longitude
            },
            stopover: true
        }));



    directionsService.route({

        origin,

        destination,

        waypoints,

        travelMode:
            google.maps.TravelMode.DRIVING

    })

    .then(response => {

        directionsRenderer.setDirections(response);

    })

    .catch(error => {

        console.error(
            "Could not create route:",
            error
        );

    });

}*/



//let map;
//let routePolyline;


/*async function initMap(){

    const {Map} = await google.maps.importLibrary("maps");
    const {AdvancedMarkerElement} =
        await google.maps.importLibrary("marker");


    map = new Map(
        document.querySelector("gmp-map"),
        {
            zoom: 12,
            center:{
                lat:49.2827,
                lng:-123.1207
            }
        }
    );

}*/


async function createRoute() {

    const input = document.getElementById("locations").value;

    const placeNames = input
        .split("\n")
        .map(x => x.trim())
        .filter(Boolean);


    if (placeNames.length < 2) {
        alert("Please enter at least two locations");
        return;
    }


    const locations = [];


    for (const place of placeNames) {

        try {

            const location = await geocodeLocation(place);
            locations.push(location);

        } catch (error) {

            alert(error);
            return;

        }
    }

    currentRoute = {
        name: "My Route",
        description: "Created from map",
        shared: false,
        locations: locations
    };

    displayRoute(locations);
}

async function uploadRoute() {

    if (!currentRoute) {
        alert("Create a route before uploading");
        return;
    }

    const response = await fetch("/api/routes/create", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(currentRoute)
    });

    if (!response.ok) {
        alert("Could not upload route");
        return;
    }

    const savedRoute = await response.json();

    // Remember the database ID for sharing later
    currentRoute.id = savedRoute.id;

    alert("Route uploaded successfully");

}


async function displayRoute(locations) {

    if (!mapReady) {
        alert("Map is still loading");
        return;
    }

    if (!locations || locations.length < 2) {
        alert("A route needs at least 2 locations");
        return;
    }

    if (directionsRenderer !== null) {
    directionsRenderer.setMap(null);
    directionsRenderer = null;
}


    const { DirectionsService, DirectionsRenderer } =
        await google.maps.importLibrary("routes");


    const directionsService = new DirectionsService();


    directionsRenderer = new DirectionsRenderer({
        suppressMarkers: false,
        preserveViewport: false
    });


    directionsRenderer.setMap(innerMap);


    const origin = {
        lat: locations[0].latitude,
        lng: locations[0].longitude
    };


    const destination = {
        lat: locations[locations.length - 1].latitude,
        lng: locations[locations.length - 1].longitude
    };


    const waypoints = locations
        .slice(1, -1)
        .map(location => ({
            location: {
                lat: location.latitude,
                lng: location.longitude
            },
            stopover: true
        }));


    directionsService.route({
        origin: origin,
        destination: destination,
        waypoints: waypoints,
        travelMode: google.maps.TravelMode.DRIVING

    })
    .then(response => {

        directionsRenderer.setDirections(response);

    })
    .catch(error => {

        console.error("Route error:", error);

    });
}

async function geocodeLocation(address) {
    const geocoder = new google.maps.Geocoder();

    return new Promise((resolve, reject) => {
        geocoder.geocode({ address: address }, (results, status) => {

            if (status === "OK" && results.length > 0) {
                const point = results[0].geometry.location;

                resolve({
                    name: address,
                    latitude: point.lat(),
                    longitude: point.lng()
                });
            } else {
                reject("Could not find location: " + address);
            }
        });
    });
}



async function shareRoute() {

    if (!currentRoute || !currentRoute.id) {
        alert("Upload the route first");
        return;
    }

    const response = await fetch("/routes/" + currentRoute.id + "/share", {
        method: "POST"
    });

    if (!response.ok) {
        alert("Could not share route");
        return;
    }

    const shareUrl =
        window.location.origin +
        "/route-shared/" +
        currentRoute.id;

    document.getElementById("shareLink").value = shareUrl;
    document.getElementById("shareSection").style.display = "block";
}

document
.getElementById("createRoute")
.onclick = createRoute;


document
.getElementById("saveRoute")
.onclick = uploadRoute;

document
.getElementById("shareRoute")
.onclick = shareRoute;

//initMap();

void createMap();