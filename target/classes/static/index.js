const mapElement = document.querySelector('gmp-map');
const typeSelect = document.querySelector('.type-select');
var innerMap, currentLocationDot, infoWindow;

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

    infoWindow = new InfoWindow();
    innerMap = mapElement.innerMap;

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
  markers.forEach(marker => {
    if (marker != currentLocationDot){
      marker.map = null;
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
    places.forEach((place) => {
      const marker = new AdvancedMarkerElement({
        map: innerMap,
        position: place.location,
      });
      const info = `
        <div>
          <strong>${place.displayName}</strong>
          <div>${place.formattedAddress}</div>
        </div>
      `;
      marker.addListener("click", () => {
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

void createMap();