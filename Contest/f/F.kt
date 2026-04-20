import java.util.*

// Submission id 157667402
data class Animal(val id: Int, val type: Animal.Type, val height: Int, var keeperId: Int?) {
    enum class Type {
        CAT,
        DOG,
        HIPPO,
        HORSE,
        FISH
    }
}

fun printAnimalIds(animals: SortedSet<Animal>?) {
    if (animals.isNullOrEmpty()) {
        println("NONE")
    }
    else {
        println(animals.joinToString(" ") { it.id.toString() })
    }
}

data class Keeper(val id: Int, val name: String)

class ZooDatabase {
    private val idToAnimal = mutableMapOf<Int, Animal>()
    private val idToKeeper = mutableMapOf<Int, Keeper>()

    private val animalComparator = compareBy<Animal>({ it.height }, { it.id })
    private val animals = TreeSet(animalComparator)
    private val typeToAnimals = mutableMapOf<Animal.Type, TreeSet<Animal>>()
    private val talkingAnimals = TreeSet(animalComparator)
    private val keeperIdToAnimals = mutableMapOf<Int, TreeSet<Animal>>()
    private val keeperNameToAnimals = mutableMapOf<String, TreeSet<Animal>>()

    fun addAnimal(id: Int, type: Animal.Type, height: Int) {
        val animal = Animal(id, type, height, null)
        idToAnimal[id] = animal
        animals.add(animal)
        typeToAnimals.getOrPut(type) { TreeSet(animalComparator) }.add(animal)
        if (type != Animal.Type.FISH) talkingAnimals.add(animal)
    }

    fun deleteAnimal(animalId: Int) {
        unassignAnimal(animalId)
        val animal = idToAnimal.remove(animalId) ?: return
        animals.remove(animal)
        typeToAnimals[animal.type]?.remove(animal)
        if (animal.type != Animal.Type.FISH) talkingAnimals.remove(animal)
    }

    fun addKeeper(id: Int, name: String) {
        idToKeeper[id] = Keeper(id, name)
    }

    fun assignAnimalToKeeper(animalId: Int, keeperId: Int) {
        val animal = idToAnimal[animalId] ?: return
        val keeper = idToKeeper[keeperId] ?: return

        if (animal.keeperId == keeperId) return
        unassignAnimal(animalId)
        keeperIdToAnimals.getOrPut(keeperId) { TreeSet(animalComparator) }.add(animal)
        keeperNameToAnimals.getOrPut(keeper.name) { TreeSet(animalComparator) }.add(animal)
        animal.keeperId = keeperId
    }

    fun unassignAnimal(animalId: Int) {
        val animal = idToAnimal[animalId] ?: return
        animal.keeperId?.let { keeperId ->
            keeperIdToAnimals[keeperId]?.remove(animal)
            idToKeeper[keeperId]?.let { keeper -> keeperNameToAnimals[keeper.name]?.remove(animal) }
        }

        animal.keeperId = null
    }

    fun getAnimalsByKeeperId(keeperId: Int): SortedSet<Animal> = keeperIdToAnimals[keeperId] ?: TreeSet(animalComparator)

    fun getAnimalsByKeeperName(name: String): SortedSet<Animal> = keeperNameToAnimals[name] ?: TreeSet(animalComparator)

    fun getTallAnimals(minHeightExclusive: Int): SortedSet<Animal> = animals.tailSet(Animal(Int.MIN_VALUE, Animal.Type.CAT, minHeightExclusive + 1, null))

    fun getTalkingAnimals(): SortedSet<Animal> = talkingAnimals

    fun getAnimalsByType(type: Animal.Type): SortedSet<Animal> = typeToAnimals[type] ?: TreeSet(animalComparator)
}

fun main() {
    val scanner = Scanner(System.`in`)
    val q = scanner.nextInt()
    scanner.nextLine()
    val zooDatabase = ZooDatabase()

    repeat(q) {
        val line = scanner.nextLine()
        val words = line.split(' ')
        when (words[0]) {
            "ANIMAL_ADD" -> {
                val id = words[1].toInt()
                val type = Animal.Type.valueOf(words[2])
                val height = words[3].toInt()
                zooDatabase.addAnimal(id, type, height)
            }
            "ANIMAL_DEL" -> {
                val animalId = words[1].toInt()
                zooDatabase.deleteAnimal(animalId)
            }
            "KEEPER_ADD" -> {
                val id = words[1].toInt()
                val name = words[2]
                zooDatabase.addKeeper(id, name)
            }
            "ASSIGN" -> {
                val animalId = words[1].toInt()
                val keeperId = words[2].toInt()
                zooDatabase.assignAnimalToKeeper(animalId, keeperId)
            }
            "UNASSIGN" -> {
                val animalId = words[1].toInt()
                zooDatabase.unassignAnimal(animalId)
            }
            "Q_KEEPER" -> {
                val keeperId = words[1].toInt()
                printAnimalIds(zooDatabase.getAnimalsByKeeperId(keeperId))
            }
            "Q_NAME" -> {
                val name = words[1]
                printAnimalIds(zooDatabase.getAnimalsByKeeperName(name))
            }
            "Q_TALL" -> {
                val h = words[1].toInt()
                printAnimalIds(zooDatabase.getTallAnimals(h))
            }
            "Q_SOUND" -> {
                printAnimalIds(zooDatabase.getTalkingAnimals())
            }
            "Q_TYPE" -> {
                val type = Animal.Type.valueOf(words[1])
                printAnimalIds(zooDatabase.getAnimalsByType(type))
            }
        }
    }
}
