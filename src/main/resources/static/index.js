const mapElement = document.querySelector('gmp-map');
const typeSelect = document.querySelector('.type-select');
var innerMap, currentLocationDot, infoWindow;
const marker = document.getElementById("marker");
const latInput = document.getElementById("lat-input");
const lngInput = document.getElementById("lng-input");
const showSavedBtn = document.getElementById('show-saved-btn');
const savedLocationsPanel = document.getElementById('saved-locations-panel');

function createCurrentDot(){
  const div = document.createElement('div');
  div.style.width = "16px";
  div.style.height = "16px";
  div.style.backgroundColor = "blue";
  div.style.borderRadius = "50%";
  return div;
}

window.savePlace = function(name, lat, lng){
  const savedOnes = savedLocationsPanel.innerText || savedLocationsPanel.textContent;

  if(savedOnes.includes(name)){
    alert("This location is already saved");
    return;
  }

  document.getElementById('lat-input').value = lat;
  document.getElementById('lng-input').value = lng;
  document.getElementById('name-input').value = name;
  document.getElementById('save-form').submit();  
}

async function createMap() {
    const { Map, InfoWindow } = await google.maps.importLibrary('maps');
    const { AdvancedMarkerElement } = await google.maps.importLibrary('marker');
    const { Geocoder } = await google.maps.importLibrary('geocoding');

    infoWindow = new InfoWindow();
    innerMap = mapElement.innerMap;
    const geocoder = new Geocoder();

    innerMap.setOptions({
        mapTypeControl: false,
        clickableIcons: false,
    });

    marker.addListener("click", () => {
      infoWindow.open({
        anchor: marker,
        map: innerMap,
      });
    });

    innerMap.addListener("click", (e) => {
      if (!e.latLng){
        return;
      }
      infoWindow.close();
      const lat = e.latLng.lat();
      const lng = e.latLng.lng();

      marker.position = { lat, lng };
      marker.map = innerMap;

      latInput.value = lat;
      lngInput.value = lng;

      geocoder.geocode({location: {lat, lng}}, (results, status) => {
        const address = (status === "OK" && results[0]) ? results[0].formatted_address : "Address not found";
        
        const nameInput = document.getElementById("name-input");
        if (nameInput){
          nameInput.value = address;
        }
        
        const selectedInfo = `
          <div>
            <strong>Selected Location</strong>
            <div>${address}</div>
            <button onclick="document.getElementById('save-form').submit();" id="save-location-btn" style="margin-top: 10px;">Save This Location</button>
          </div>
        `;
        infoWindow.setContent(selectedInfo);
        infoWindow.open({
          anchor: marker,
          map: innerMap,
        });
      });
    })

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

showSavedBtn.addEventListener('click', () => {
  if (savedLocationsPanel.style.display === 'none'){
    savedLocationsPanel.style.display = 'block';
    showSavedBtn.textContent = 'Hide Saved Location';
  }
  else{
    savedLocationsPanel.style.display = 'none';
    showSavedBtn.textContent = 'Show Saved Location';
  }
})

window.panToLocation = function(lat, lng){
  if (!innerMap){
    return;
  }
  const targetPos = {lat: Number(lat), lng: Number(lng)};
  innerMap.setCenter(targetPos);
  innerMap.setZoom(15);

  marker.position = targetPos;
  marker.map = innerMap;

  infoWindow.close();
}

void createMap();