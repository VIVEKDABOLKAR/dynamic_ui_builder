import { createContext, useContext, useEffect, useState } from "react";
import { changeFacilityRequest, getAccessibleFacilities } from "../api/facilityApi";

const FacilityContext = createContext();

export function FacilityProvider({ children }) {
    const [facilities, setFacilities] = useState([]);
    const [selectedFacility, setSelectedFacility] = useState(null);
    const [loading, setLoading] = useState(true);

    // Load accessible facilities on app startup
    useEffect(() => {
        loadFacilities();
    }, []);

    const loadFacilities = async () => {
        try {
            setLoading(true);
            
            const data = await getAccessibleFacilities();
            console.log(data)

            setFacilities(data || []);

            if (!data?.length) {
                setSelectedFacility(null);
                return;
            }

            // Restore previous selection if available
            const storedFacilityId = localStorage.getItem("facilityId");

            const facility =
                data.find((f) => String(f.id) === storedFacilityId) || data[0];

            setSelectedFacility(facility);

            localStorage.setItem("facilityId", facility.id);
        } catch (err) {
            console.error("Failed to load facilities", err);
        } finally {
            setLoading(false);
        }
    };

    const changeFacility = async (facilityId) => {
        try {
            const selectedFacility = facilities.find((f) => f.id === facilityId)

            if(!selectedFacility) {
                throw new Error("facilityid is not allowed");
                
            }

            const res = await changeFacilityRequest(facilityId);

            //add check for undefined selected facility
            setSelectedFacility(selectedFacility);

            localStorage.setItem("facilityId", selectedFacility?.id);

            
        } catch (err) {
            console.error(err);
        }
    };

    return (
        <FacilityContext.Provider
            value={{
                facilities,
                selectedFacility,
                loading,
                changeFacility,
                reloadFacilities: loadFacilities,
            }}
        >
            {children}
        </FacilityContext.Provider>
    );
}

export const useFacility = () => useContext(FacilityContext);