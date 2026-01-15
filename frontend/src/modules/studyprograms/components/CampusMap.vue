<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import * as L from 'leaflet'
import 'leaflet/dist/leaflet.css'
import type { CampusBuilding } from '../model/CampusBuilding'


const props = defineProps<{
  buildings?: CampusBuilding[]
}>()

const mapEl = ref<HTMLElement | null>(null)
let map: L.Map | null = null
let markers: L.LayerGroup | null = null

// Fix for missing marker icons in Vite builds
// Use the leaflet CDN images or correct relative paths
const DefaultIcon = L.icon({
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
})

function initMap() {
  if (!mapEl.value) return
  if (map) return

  map = L.map(mapEl.value, {
    zoomControl: true,
    scrollWheelZoom: false, // read-only UX
  })

  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    // OSM attribution required
    attribution: '&copy; OpenStreetMap contributors',
  }).addTo(map)

  markers = L.layerGroup().addTo(map)

  renderMarkers()
}

function renderMarkers() {
  if (!map || !markers) return
  markers.clearLayers()

  const list = (props.buildings || []).filter(b => Number.isFinite(b.lat) && Number.isFinite(b.lon))

  if (list.length === 0) {
    // fallback view (Vienna-ish)
    map.setView([48.2082, 16.3738], 12)
    return
  }

  const bounds = L.latLngBounds([])

  for (const b of list) {
    const latlng = L.latLng(b.lat, b.lon)
    bounds.extend(latlng)

    const popupHtml = `
      <div style="min-width: 180px">
        <div style="font-weight: 600; margin-bottom: 4px">${escapeHtml(b.label)}</div>
        <div style="font-size: 12px; opacity: 0.85">${escapeHtml(b.address)}</div>
      </div>
    `

    L.marker(latlng, { icon: DefaultIcon })
      .addTo(markers)
      .bindPopup(popupHtml)
      .bindTooltip(b.label, { direction: 'top', offset: [0, -10], permanent: false })
  }

  map.fitBounds(bounds.pad(0.25))
}

function escapeHtml(s: string) {
  return s.replace(/[&<>"']/g, (c) => {
    switch (c) {
      case '&': return '&amp;'
      case '<': return '&lt;'
      case '>': return '&gt;'
      case '"': return '&quot;'
      case "'": return '&#39;'
      default: return c
    }
  })
}

watch(
  () => props.buildings,
  () => {
    // If map exists, re-render markers when buildings change
    renderMarkers()
  },
  { deep: true }
)

onMounted(initMap)

onBeforeUnmount(() => {
  if (map) {
    map.remove()
    map = null
    markers = null
  }
})
</script>

<template>
  <div class="campus-map-wrapper">
    <div class="campus-map-header">
      <h3>Campus location</h3>
      <p class="hint" v-if="!buildings || buildings.length === 0">
        No campus location available for this program.
      </p>
    </div>

    <div ref="mapEl" class="campus-map"></div>

    <div v-if="buildings && buildings.length > 0" class="campus-map-legend">
      <div class="legend-title">Buildings</div>
      <ul>
        <li v-for="b in buildings" :key="b.label + b.address">
          <strong>{{ b.label }}</strong> — {{ b.address }}
        </li>
      </ul>
    </div>
  </div>
</template>

<style scoped>
.campus-map-wrapper {
  margin-top: 20px;
}
.campus-map-header h3 {
  margin: 0 0 6px 0;
}
.hint {
  margin: 0 0 10px 0;
  opacity: 0.8;
  font-size: 13px;
}
.campus-map {
  height: 320px;   
  width: 100%;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid rgba(0,0,0,0.08);
}
.campus-map-legend {
  margin-top: 10px;
  font-size: 13px;
}
.legend-title {
  font-weight: 600;
  margin-bottom: 6px;
}
.campus-map-legend ul {
  margin: 0;
  padding-left: 18px;
}
</style>
