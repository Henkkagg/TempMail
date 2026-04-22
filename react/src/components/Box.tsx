import { useState } from "react"
import InfoRow from "./InfoRow"
import useEventStream from "../api/useEventStream"
import ServiceInfo from "./ServiceInfo"

function Box() {

    const [selectedMail, setSelectedMail] = useState(0)

    function handleSelectedMailChange(id: number) {
        setSelectedMail(id)
    }

    const [copiedRecently, setCopiedRecently] = useState(false)
    function handleCopying() {
        navigator.clipboard.writeText(emailAddress!!)
        setCopiedRecently(true)
        setTimeout(() => setCopiedRecently(false), 2000)
    }

    function renderBodyWithLinks(body: string) {
    const urlRegex = /(https?:\/\/[^\s]+)/g
    const parts = body.split(urlRegex)
    
    return parts.map((part, index) => {
        if (urlRegex.test(part)) {
            return <a key={index} href={part} target="_blank" rel="noreferrer" className="text-blue-500 underline">{part}</a>
        }
        return part
    })
}


    const { emailAddress, emails } = useEventStream()

    if (!emailAddress || !emails) return null

    return (

        <div className="
        flex flex-col
        justify-center
        h-screen md:h-[90vh]
        w-full md:w-[80%] lg:w-[60%] xl:w-[50%]
        md:border-border md:border-2 md:rounded-2xl shadow-border shadow-2xl
        overflow-hidden
        ">
            <div className="flex flex-col items-center bg-background">
                <span className="text-sm">{import.meta.env.VITE_CLICKTOCOPY}</span>
                <span
                    className="w-full text-center text-2xl cursor-pointer rounded-md wrap-break-word"
                    onClick={handleCopying}
                >{copiedRecently ? <span>{import.meta.env.VITE_COPYSUCCESS}</span> : <span>{emailAddress?.replace('@', '\u200B@')?.replace(/\.(.*)/, '\u200B.$1')}</span>}</span>
            </div>
            <div className="
            flex flex-col h-1/6 flex-none
            bg-background
            rounded">
                <div className="
                flex w-full
                bg-surface
                ">
                    <span className="flex flex-1 justify-center">{import.meta.env.VITE_SENDER}</span>
                    <span className="flex flex-1 justify-center">{import.meta.env.VITE_SUBJECT}</span>
                </div>
                <div className="overflow-y-auto">
                    {
                        emails.length === 0
                            ? <InfoRow sender={import.meta.env.VITE_INTROSENDER} subject={import.meta.env.VITE_INTROSUBJECT} isSelected={true} onClick={() => { }}></InfoRow>
                            : emails.map((mail, index) =>
                                <InfoRow sender={mail.sender} subject={mail.subject} isSelected={selectedMail === index} onClick={() => handleSelectedMailChange(index)}></InfoRow>
                            )
                    }
                </div>


            </div>
            <div className="flex flex-1 flex-col overflow-hidden">
                <hr className="border-border border"></hr>
                <div className="flex-1 bg-background px-1 whitespace-pre-wrap overflow-y-auto rounded-2xl">
                    {emails.length === 0
                        ? <ServiceInfo emailAddress={emailAddress}></ServiceInfo> 
                        : renderBodyWithLinks(emails[selectedMail].body)
                    }
                </div>
            </div>
        </div>
    )
}

export default Box