interface Props {
    sender: string
    subject: string
    isSelected: boolean
    onClick: () => void
}


function InfoRow({ sender, subject, isSelected, onClick }: Props) {

    return (
        <div 
        className={`flex w-full gap-2 cursor-pointer ${isSelected ? "bg-primary" : "bg-background"}`}
        onClick={onClick}
        >
            <div className="flex flex-1 min-w-0 justify-center">
                <span className="truncate">{sender}</span>
            </div>

            <div className="flex flex-1 min-w-0 justify-center">
                <span className="truncate">{subject}</span>
            </div>
        </div>
    )
}

export default InfoRow