export async function getToken(): Promise<string> {
    const response = await fetch("/api/token", { credentials: 'include' })
    const data = await response.json()
    return data.address
}

export default getToken